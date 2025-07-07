package com.sprint.mission.discodeit.storage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.sprint.mission.discodeit.entity.BinaryContent;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
public class S3BinaryContentStorage implements BinaryContentStorage {

  private final String accessKey;
  private final String secretKey;
  private final String region;
  private final String bucket;
  private final long presignedUrlExpiration;

  private S3Client s3Client;
  private S3Presigner s3Presigner;

  @Autowired
  public S3BinaryContentStorage(
      @Value("${discodeit.storage.s3.access-key}") String accessKey,
      @Value("${discodeit.storage.s3.secret-key}") String secretKey,
      @Value("${discodeit.storage.s3.region}") String region,
      @Value("${discodeit.storage.s3.bucket}") String bucket,
      @Value("${discodeit.storage.s3.presigned-url-expiration}") long presignedUrlExpiration) {
    this.accessKey = accessKey;
    this.secretKey = secretKey;
    this.region = region;
    this.bucket = bucket;
    this.presignedUrlExpiration = presignedUrlExpiration;
    initializeS3Client();
  }

  // 테스트용 생성자 default(package-private)
  S3BinaryContentStorage(String accessKey, String secretKey, String region, String bucket,
      long presignedUrlExpiration, S3Client s3Client, S3Presigner s3Presigner) {
    this.accessKey = accessKey;
    this.secretKey = secretKey;
    this.region = region;
    this.bucket = bucket;
    this.presignedUrlExpiration = presignedUrlExpiration;
    this.s3Client = s3Client;
    this.s3Presigner = s3Presigner;
  }

  private void initializeS3Client() {
    AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKey, secretKey);
    Region awsRegion = Region.of(region);

    this.s3Client = S3Client.builder()
        .region(awsRegion)
        .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
        .build();

    this.s3Presigner = S3Presigner.builder()
        .region(awsRegion)
        .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
        .build();
  }

  @Override
  public UUID put(UUID id, byte[] content) {
    try {
      PutObjectRequest putObjectRequest = PutObjectRequest.builder()
          .bucket(bucket)
          .key(id.toString())
          .build();

      s3Client.putObject(putObjectRequest, RequestBody.fromBytes(content));
      return id;
    } catch (Exception e) {
      throw new RuntimeException("S3 파일 업로드 실패: ", e);
    }
  }

  @Override
  public InputStream get(UUID id) {
    try {
      GetObjectRequest getObjectRequest = GetObjectRequest.builder()
          .bucket(bucket)
          .key(id.toString())
          .build();

      ResponseInputStream<GetObjectResponse> responseInputStream = s3Client.getObject(getObjectRequest);

      // ResponseInputStream을 byte array로 읽어서 ByteArrayInputStream으로 변환
      byte[] content = responseInputStream.readAllBytes();
      responseInputStream.close();

      return new ByteArrayInputStream(content);
    } catch (IOException e) {
      throw new RuntimeException("S3 파일 조회 실패: ", e);
    }
  }

  @Override
  public ResponseEntity<Resource> download(BinaryContent binaryContentData) {
    try {
      String presignedUrl = generatePresignedUrl(binaryContentData.getId().toString(),
          binaryContentData.getContentType());

      return ResponseEntity.status(HttpStatus.FOUND)
          .header(HttpHeaders.LOCATION, presignedUrl)
          .build();
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  public S3Client getS3Client() {
    return s3Client;
  }

  public String generatePresignedUrl(String key, String contentType) {
    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .build();

    GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
        .signatureDuration(Duration.ofSeconds(presignedUrlExpiration))
        .getObjectRequest(getObjectRequest)
        .build();

    PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
    return presignedRequest.url().toString();
  }
}
