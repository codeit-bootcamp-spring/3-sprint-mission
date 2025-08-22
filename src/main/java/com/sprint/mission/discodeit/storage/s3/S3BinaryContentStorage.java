package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Duration;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

/**
 * AWS S3를 이용한 BinaryContent 저장소 구현체
 *
 * - 파일 업로드 (put)
 * - 파일 다운로드 (get)
 * - Presigned URL 기반 다운로드 리다이렉트 (download)
 */
@Slf4j
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
@Component
public class S3BinaryContentStorage implements BinaryContentStorage {

  // AWS 인증 정보 및 버킷 설정값
  private final String accessKey;
  private final String secretKey;
  private final String region;
  private final String bucket;

  // Presigned URL 만료시간 (기본값 600초 = 10분)
  @Value("${discodeit.storage.s3.presigned-url-expiration:600}")
  private long presignedUrlExpirationSeconds;

  // 생성자 주입 방식으로 application.yml 값 매핑
  public S3BinaryContentStorage(
      @Value("${discodeit.storage.s3.access-key}") String accessKey,
      @Value("${discodeit.storage.s3.secret-key}") String secretKey,
      @Value("${discodeit.storage.s3.region}") String region,
      @Value("${discodeit.storage.s3.bucket}") String bucket
  ) {
    this.accessKey = accessKey;
    this.secretKey = secretKey;
    this.region = region;
    this.bucket = bucket;
  }

  /**
   * 파일을 S3에 업로드
   *
   * @param binaryContentId 업로드할 파일의 UUID (S3 key)
   * @param bytes           파일 바이트 배열
   * @return 저장된 파일의 UUID
   */
  @Override
  public UUID put(UUID binaryContentId, byte[] bytes) {
    String key = binaryContentId.toString();
    try {
      S3Client s3Client = getS3Client();

      // S3 업로드 요청 객체 생성
      PutObjectRequest request = PutObjectRequest.builder()
          .bucket(bucket)
          .key(key)
          .build();

      // 파일 업로드 실행
      s3Client.putObject(request, RequestBody.fromBytes(bytes));
      log.info("S3에 파일 업로드 성공: {}", key);

      return binaryContentId;
    } catch (S3Exception e) {
      log.error("S3에 파일 업로드 실패: {}", e.getMessage());
      throw new RuntimeException("S3에 파일 업로드 실패: " + key, e);
    }
  }

  /**
   * 파일을 S3에서 다운로드
   *
   * @param binaryContentId 다운로드할 파일의 UUID
   * @return 파일 InputStream
   */
  @Override
  public InputStream get(UUID binaryContentId) {
    String key = binaryContentId.toString();
    try {
      S3Client s3Client = getS3Client();

      // S3 다운로드 요청 객체 생성
      GetObjectRequest request = GetObjectRequest.builder()
          .bucket(bucket)
          .key(key)
          .build();

      // 파일 다운로드 (바이트 배열 변환 후 스트림으로 반환)
      byte[] bytes = s3Client.getObjectAsBytes(request).asByteArray();
      return new ByteArrayInputStream(bytes);
    } catch (S3Exception e) {
      log.error("S3에서 파일 다운로드 실패: {}", e.getMessage());
      throw new NoSuchElementException("File with key " + key + " does not exist");
    }
  }

  /**
   * S3Client 생성 (매번 새로 생성)
   *
   * @return S3Client
   */
  private S3Client getS3Client() {
    return S3Client.builder()
        .region(Region.of(region))
        .credentialsProvider(
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey, secretKey)
            )
        )
        .build();
  }

  /**
   * Presigned URL 기반 다운로드
   * 클라이언트가 Presigned URL로 직접 S3에 접근하도록 리다이렉트
   */
  @Override
  public ResponseEntity<Void> download(BinaryContentDto metaData) {
    try {
      String key = metaData.id().toString();

      // Presigned URL 생성
      String presignedUrl = generatePresignedUrl(key, metaData.contentType());
      log.info("생성된 Presigned URL: {}", presignedUrl);

      // 302 Redirect 응답 반환
      return ResponseEntity
          .status(HttpStatus.FOUND)
          .header(HttpHeaders.LOCATION, presignedUrl)
          .build();
    } catch (Exception e) {
      log.error("Presigned URL 생성 실패: {}", e.getMessage());
      throw new RuntimeException("Presigned URL 생성 실패", e);
    }
  }

  /**
   * Presigned URL 생성
   *
   * @param key         파일 키(UUID 문자열)
   * @param contentType 응답 Content-Type
   * @return Presigned URL 문자열
   */
  private String generatePresignedUrl(String key, String contentType) {
    try (S3Presigner presigner = getS3Presigner()) {
      // Presigned URL 요청을 위한 GetObjectRequest 생성
      GetObjectRequest getObjectRequest = GetObjectRequest.builder()
          .bucket(bucket)
          .key(key)
          .responseContentType(contentType)
          .build();

      // Presign 요청 생성 (만료 시간 설정)
      GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
          .signatureDuration(Duration.ofSeconds(presignedUrlExpirationSeconds))
          .getObjectRequest(getObjectRequest)
          .build();

      // Presigned URL 생성
      PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);
      return presignedRequest.url().toString();
    }
  }

  /**
   * S3Presigner 생성 (Presigned URL 전용)
   */
  private S3Presigner getS3Presigner() {
    return S3Presigner.builder()
        .region(Region.of(region))
        .credentialsProvider(
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey, secretKey)
            )
        )
        .build();
  }
}