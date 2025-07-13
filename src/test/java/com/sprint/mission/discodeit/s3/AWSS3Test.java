package com.sprint.mission.discodeit.s3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Properties;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

public class AWSS3Test {

  private static S3Client s3;
  private static S3Presigner presigner;
  private static String bucket;
  private static final String TEST_KEY = "test-upload.txt";
  private static final String TEST_CONTENT = "Hello S3!";

  @BeforeAll
  public static void setup() {
    S3TestConfig config = new S3TestConfig();

    s3 = S3Client.builder()
        .region(Region.of(config.get("AWS_S3_REGION")))
        .credentialsProvider(StaticCredentialsProvider.create(
            AwsBasicCredentials.create(
                config.get("AWS_S3_ACCESS_KEY"),
                config.get("AWS_S3_SECRET_KEY")
            )))
        .build();

    presigner = S3Presigner.builder()
        .region(Region.of(config.get("AWS_S3_REGION")))
        .credentialsProvider(StaticCredentialsProvider.create(
            AwsBasicCredentials.create(
                config.get("AWS_S3_ACCESS_KEY"),
                config.get("AWS_S3_SECRET_KEY")
            )))
        .build();

    bucket = config.get("AWS_S3_BUCKET");
  }

  @Test
  void testUpload() {
    PutObjectRequest putRequest = PutObjectRequest.builder()
        .bucket(bucket)
        .key(TEST_KEY)
        .contentType("text/plain")
        .build();

    s3.putObject(putRequest, RequestBody.fromString(TEST_CONTENT));
  }

  @Test
  void testDownload() {
    GetObjectRequest getRequest = GetObjectRequest.builder()
        .bucket(bucket)
        .key(TEST_KEY)
        .build();

    String content = new String(s3.getObjectAsBytes(getRequest).asByteArray());
    assertEquals(TEST_CONTENT, content);
  }

  @Test
  void testPresignedUrl() {
    GetObjectRequest getRequest = GetObjectRequest.builder()
        .bucket(bucket)
        .key(TEST_KEY)
        .build();

    GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
        .signatureDuration(Duration.ofMinutes(10))
        .getObjectRequest(getRequest)
        .build();

    URL url = presigner.presignGetObject(presignRequest).url();
    assertNotNull(url);

    System.out.println("Presigned URL: " + url);
  }

  // 환경변수 .env 파일을 로드하는 내부 클래스
  static class S3TestConfig {

    private final Properties properties = new Properties();

    public S3TestConfig() {
      try (FileInputStream input = new FileInputStream(Paths.get(".env").toFile())) {
        properties.load(input);
      } catch (IOException e) {
        throw new RuntimeException(".env 파일을 프로젝트 루트에서 로드하는 중 오류 발생", e);
      }
    }

    public String get(String key) {
      return properties.getProperty(key);
    }
  }
}
