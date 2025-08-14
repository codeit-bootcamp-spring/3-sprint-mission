package com.sprint.mission.discodeit.storage;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.sprint.mission.discodeit.support.TestEnvConfig;
import com.sprint.mission.discodeit.support.TestUtils;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@Tag("integration")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
class AWSS3Test {

  private static final Logger log = LoggerFactory.getLogger(AWSS3Test.class);
  private S3Client s3Client;
  private S3Presigner s3Presigner;
  private String bucketName;
  private String testObjectKey;
  private final Set<String> uploadedKeys = new HashSet<>();

  @Autowired
  TestEnvConfig testEnvConfig;

  @BeforeEach
  void setUp() {
    initializeS3Client();
    testObjectKey = "test-files/test-" + System.currentTimeMillis() + ".txt";
  }

  private void initializeS3Client() {
    AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(
        testEnvConfig.awsS3AccessKey,
        testEnvConfig.awsS3SecretKey);

    Region region = Region.of(testEnvConfig.awsS3Region);

    s3Client = S3Client.builder()
        .region(region)
        .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
        .build();

    s3Presigner = S3Presigner.builder()
        .region(region)
        .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
        .build();

    bucketName = testEnvConfig.awsS3Bucket;
  }

  @Test
  void 환경변수_로드_확인() {
    log.info("=== 환경변수 로드 확인 ===");
    log.info("AWS_S3_ACCESS_KEY: {}", TestUtils.maskSensitiveValue(testEnvConfig.awsS3AccessKey));
    log.info("AWS_S3_SECRET_KEY: {}", TestUtils.maskSensitiveValue(testEnvConfig.awsS3SecretKey));
    log.info("AWS_S3_REGION: {}", testEnvConfig.awsS3Region);
    log.info("AWS_S3_BUCKET: {}", testEnvConfig.awsS3Bucket);
    log.info("=======================");

    // 기본 검증
    assertFalse(testEnvConfig.awsS3AccessKey.isEmpty(), "AWS_S3_ACCESS_KEY가 설정되어야 합니다");
    assertFalse(testEnvConfig.awsS3SecretKey.isEmpty(), "AWS_S3_SECRET_KEY가 설정되어야 합니다");
    assertFalse(testEnvConfig.awsS3Bucket.isEmpty(), "AWS_S3_BUCKET이 설정되어야 합니다");
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
  void 파일_업로드와_삭제_테스트() throws IOException {
    // given
    String testContent = "테스트 파일 내용 - " + System.currentTimeMillis();
    Path tempFile = createTempFile(testContent);

    try {
      PutObjectRequest putObjectRequest = PutObjectRequest.builder()
          .bucket(bucketName)
          .key(testObjectKey)
          .contentType("text/plain")
          .build();
      PutObjectResponse response = s3Client.putObject(
          putObjectRequest,
          RequestBody.fromFile(tempFile));

      assertNotNull(response.eTag(), "업로드된 파일의 ETag가 존재해야 합니다.");
      assertTrue(isObjectExists(testObjectKey), "업로드된 객체가 S3에 존재해야 합니다.");
      uploadedKeys.add(testObjectKey);
    } finally {
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
    uploadedKeys.add(testObjectKey);
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
    uploadedKeys.add(testObjectKey);

    log.info("생성된 Presigned URL: {}", presignedUrl);
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
      uploadedKeys.add(key);
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
