package com.sprint.mission.discodeit.storage.s3;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Properties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.cdimascio.dotenv.Dotenv;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

class AWSS3Test {

  private static final Logger log = LoggerFactory.getLogger(AWSS3Test.class);
  private S3Client s3Client;
  private S3Presigner s3Presigner;
  private String bucketName;
  private String testObjectKey;
  private Properties awsProperties;
  private Dotenv dotenv;

  @BeforeEach
  void setUp() {
    loadAwsProperties();
    initializeS3Client();
    testObjectKey = "test-files/test-" + System.currentTimeMillis() + ".txt";
  }

  private void loadAwsProperties() {
    awsProperties = new Properties();

    // .env 파일 로드
    dotenv = Dotenv.configure()
        .directory(System.getProperty("user.dir"))
        .ignoreIfMissing()
        .load();

    awsProperties.setProperty("aws.accessKeyId", getEnvOrDefault("AWS_S3_ACCESS_KEY", ""));
    awsProperties.setProperty("aws.secretAccessKey", getEnvOrDefault("AWS_S3_SECRET_KEY", ""));
    awsProperties.setProperty("aws.region", getEnvOrDefault("AWS_S3_REGION", "ap-northeast-2"));
    awsProperties.setProperty("aws.s3.bucketName", getEnvOrDefault("AWS_S3_BUCKET", ""));

    bucketName = awsProperties.getProperty("aws.s3.bucketName");

    // 필수 설정값 검증
    if (awsProperties.getProperty("aws.accessKeyId").isEmpty() ||
        awsProperties.getProperty("aws.secretAccessKey").isEmpty() ||
        bucketName.isEmpty()) {
      throw new IllegalStateException(
          "AWS 설정이 완료되지 않았습니다. .env 파일의 AWS_S3_ACCESS_KEY, AWS_S3_SECRET_KEY, AWS_S3_BUCKET을 확인하세요.");
    }
  }

  private void initializeS3Client() {
    AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(
        awsProperties.getProperty("aws.accessKeyId"),
        awsProperties.getProperty("aws.secretAccessKey"));

    Region region = Region.of(awsProperties.getProperty("aws.region"));

    s3Client = S3Client.builder()
        .region(region)
        .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
        .build();

    s3Presigner = S3Presigner.builder()
        .region(region)
        .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
        .build();
  }

  @Test
  void S3_버킷_존재_여부_확인() {
    HeadBucketRequest headBucketRequest = HeadBucketRequest.builder()
        .bucket(bucketName)
        .build();

    assertDoesNotThrow(() -> s3Client.headBucket(headBucketRequest),
        "버킷이 존재하지 않거나 접근 권한이 없습니다: " + bucketName);
  }

  @Test
  void 파일_업로드_테스트() throws IOException {
    // given
    String testContent = "테스트 파일 내용 - " + System.currentTimeMillis();
    Path tempFile = createTempFile(testContent);

    try {
      // when
      PutObjectRequest putObjectRequest = PutObjectRequest.builder()
          .bucket(bucketName)
          .key(testObjectKey)
          .contentType("text/plain")
          .build();

      PutObjectResponse response = s3Client.putObject(
          putObjectRequest,
          RequestBody.fromFile(tempFile));

      // then
      assertNotNull(response.eTag(), "업로드된 파일의 ETag가 존재해야 합니다.");
      assertTrue(isObjectExists(testObjectKey), "업로드된 객체가 S3에 존재해야 합니다.");

    } finally {
      // cleanup
      Files.deleteIfExists(tempFile);
    }
  }

  @Test
  void 파일_다운로드_테스트() throws IOException {
    String testContent = "다운로드 테스트 내용 - " + System.currentTimeMillis();
    uploadTestContent(testObjectKey, testContent);

    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
        .bucket(bucketName)
        .key(testObjectKey)
        .build();

    byte[] downloadedContent = s3Client.getObject(getObjectRequest).readAllBytes();
    String downloadedText = new String(downloadedContent);

    assertEquals(testContent, downloadedText);
  }

  @Test
  void Presigned_URL_생성_테스트() throws IOException {
    String testContent = "Presigned URL 테스트 내용 - " + System.currentTimeMillis();
    uploadTestContent(testObjectKey, testContent);

    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
        .bucket(bucketName)
        .key(testObjectKey)
        .build();

    GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
        .signatureDuration(Duration.ofMinutes(10))
        .getObjectRequest(getObjectRequest)
        .build();

    PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
    String presignedUrl = presignedRequest.url().toString();

    assertNotNull(presignedUrl, "Presigned URL이 생성되어야 합니다.");
    assertTrue(presignedUrl.contains(bucketName), "URL에 버킷 이름이 포함되어야 합니다.");
    assertTrue(presignedUrl.contains(testObjectKey), "URL에 객체 키가 포함되어야 합니다.");
    assertTrue(presignedUrl.contains("X-Amz-Signature"), "URL에 서명이 포함되어야 합니다.");

    log.info("생성된 Presigned URL: {}", presignedUrl);
  }

  @Test
  void 업로드된_파일_삭제_테스트() throws IOException {
    String testContent = "삭제 테스트 내용 - " + System.currentTimeMillis();
    uploadTestContent(testObjectKey, testContent);
    assertTrue(isObjectExists(testObjectKey), "삭제 전 객체가 존재해야 합니다.");

    DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
        .bucket(bucketName)
        .key(testObjectKey)
        .build();

    s3Client.deleteObject(deleteObjectRequest);

    assertFalse(isObjectExists(testObjectKey), "삭제 후 객체가 존재하지 않아야 합니다.");
  }

  @Test
  void 환경변수_로드_확인() {
    log.info("=== 환경변수 로드 확인 ===");
    log.info("AWS_S3_ACCESS_KEY: {}", maskSensitiveValue(getEnvOrDefault("AWS_S3_ACCESS_KEY", "")));
    log.info("AWS_S3_SECRET_KEY: {}", maskSensitiveValue(getEnvOrDefault("AWS_S3_SECRET_KEY", "")));
    log.info("AWS_S3_REGION: {}", getEnvOrDefault("AWS_S3_REGION", ""));
    log.info("AWS_S3_BUCKET: {}", getEnvOrDefault("AWS_S3_BUCKET", ""));
    log.info("=======================");

    // 기본 검증
    assertFalse(getEnvOrDefault("AWS_S3_ACCESS_KEY", "").isEmpty(), "AWS_S3_ACCESS_KEY가 설정되어야 합니다");
    assertFalse(getEnvOrDefault("AWS_S3_SECRET_KEY", "").isEmpty(), "AWS_S3_SECRET_KEY가 설정되어야 합니다");
    assertFalse(getEnvOrDefault("AWS_S3_BUCKET", "").isEmpty(), "AWS_S3_BUCKET이 설정되어야 합니다");
  }

  private String maskSensitiveValue(String value) {
    if (value == null || value.isEmpty()) {
      return "[비어있음]";
    }
    if (value.length() <= 4) {
      return "****";
    }
    return value.substring(0, 4) + "****" + value.substring(value.length() - 4);
  }

  private String getEnvOrDefault(String key, String defaultValue) {
    // 시스템 환경변수에서 먼저 찾기
    String value = System.getenv(key);
    if (value != null && !value.isEmpty()) {
      return value;
    }

    // .env 파일에서 찾기
    if (dotenv != null) {
      value = dotenv.get(key);
      if (value != null && !value.isEmpty()) {
        return value;
      }
    }

    return defaultValue;
  }

  private Path createTempFile(String content) throws IOException {
    Path tempFile = Files.createTempFile("s3-test-", ".txt");
    Files.write(tempFile, content.getBytes());
    return tempFile;
  }

  private void uploadTestContent(String key, String content) throws IOException {
    Path tempFile = createTempFile(content);
    try {
      PutObjectRequest putObjectRequest = PutObjectRequest.builder()
          .bucket(bucketName)
          .key(key)
          .contentType("text/plain")
          .build();

      s3Client.putObject(putObjectRequest, RequestBody.fromFile(tempFile));
    } finally {
      Files.deleteIfExists(tempFile);
    }
  }

  private boolean isObjectExists(String key) {
    try {
      HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
          .bucket(bucketName)
          .key(key)
          .build();
      s3Client.headObject(headObjectRequest);
      return true;
    } catch (NoSuchKeyException e) {
      return false;
    }
  }
}
