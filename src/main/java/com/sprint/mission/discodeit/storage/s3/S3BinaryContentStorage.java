package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
@Component
public class S3BinaryContentStorage implements BinaryContentStorage {

  private final S3Client s3Client;
  private final S3Presigner s3Presigner;
  private final String bucket;

  public S3BinaryContentStorage(
      @Value("${AWS_S3_ACCESS_KEY}") String accessKey,
      @Value("${AWS_S3_SECRET_KEY}") String secretKey,
      @Value("${AWS_S3_REGION}") String region,
      @Value("${AWS_S3_BUCKET}") String bucket
  ) {
    this.bucket = bucket;

    AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKey, secretKey);

    this.s3Client = S3Client.builder()
        .region(Region.of(region))
        .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
        .build();

    this.s3Presigner = S3Presigner.builder()
        .region(Region.of(region))
        .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
        .build();
  }

  @Override
  public UUID put(UUID binaryContentId, byte[] bytes) {
    PutObjectRequest putRequest = PutObjectRequest.builder()
        .bucket(bucket)
        .key(binaryContentId.toString())
        .build();

    s3Client.putObject(putRequest, software.amazon.awssdk.core.sync.RequestBody.fromBytes(bytes));
    return binaryContentId;
  }

  @Override
  public InputStream get(UUID binaryContentId) {
    GetObjectRequest getRequest = GetObjectRequest.builder()
        .bucket(bucket)
        .key(binaryContentId.toString())
        .build();

    return s3Client.getObject(getRequest);
  }

  @Override
  public ResponseEntity<Void> download(BinaryContentDto metaData) {
    // 1. Presigned URL 생성
    GetObjectRequest getRequest = GetObjectRequest.builder()
        .bucket(bucket)
        .key(metaData.id().toString())
        .build();

    GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
        .getObjectRequest(getRequest)
        .signatureDuration(Duration.ofMinutes(5)) // Presigned URL 유효시간
        .build();

    PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
    URL url = presignedRequest.url();

    // 2. 302 Redirect 응답 생성
    return ResponseEntity
        .status(HttpStatus.FOUND) // 302
        .header(HttpHeaders.LOCATION, url.toString())
        .build();
  }
}
