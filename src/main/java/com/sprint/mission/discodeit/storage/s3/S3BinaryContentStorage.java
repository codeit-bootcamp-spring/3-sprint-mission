package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Duration;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@Slf4j
@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
public class S3BinaryContentStorage implements BinaryContentStorage {

    private final String accessKey;
    private final String secretKey;
    private final String region;
    private final String bucket;
    private final int presignedUrlExpiration;
    private final NotificationRepository notificationRepository;

    @Value("${discodeit.admin.user-id:87ce8b36-8fb7-47e7-9c43-9d67ada520e4}")
    private String adminUserId;

    public S3BinaryContentStorage(
        @Value("${discodeit.storage.s3.access-key}") String accessKey,
        @Value("${discodeit.storage.s3.secret-key}") String secretKey,
        @Value("${discodeit.storage.s3.region}") String region,
        @Value("${discodeit.storage.s3.bucket}") String bucket,
        @Value("${discodeit.storage.s3.presigned-url-expiration:600}") int presignedUrlExpiration,
        NotificationRepository notificationRepository
    ) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.region = region;
        this.bucket = bucket;
        this.presignedUrlExpiration = presignedUrlExpiration;
        this.notificationRepository = notificationRepository;
    }

    @Retryable(
        retryFor = {S3Exception.class, RuntimeException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2.0)
    )
    @Override
    public UUID put(UUID binaryContentId, byte[] bytes) {
        try {
            S3Client s3Client = getS3Client();
            String key = generateS3Key(binaryContentId);

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentLength((long) bytes.length)
                .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(bytes));

            log.info("✅ S3 업로드 성공 - binaryContentId: {}, key: {}, size: {} bytes",
                binaryContentId, key, bytes.length);

            return binaryContentId;
        } catch (Exception e) {
            log.error("❌ S3 업로드 실패 - binaryContentId: {}, error: {}",
                binaryContentId, e.getMessage(), e);
            throw new RuntimeException("S3 파일 업로드 실패", e);
        }
    }

    @Recover
    public UUID recoverPut(Exception e, UUID binaryContentId, byte[] bytes) {
        String requestId = MDC.get("requestId");
        String errorMessage = String.format(
            "RequestId: %s\nBinaryContentId : %s\nError : %s",
            requestId != null ? requestId : "N/A",
            binaryContentId,
            e.getMessage()
        );

        log.error("S3 파일 업로드 최종 실패 - 관리자 알림 방송 : {}", errorMessage);

        // 관리자에게 실패 알림 방송
        try {
            Notification notification = new Notification(
                UUID.fromString(adminUserId),
                "S3 파일 업로드 실패 알림",
                errorMessage
            );
            notificationRepository.save(notification);
            log.info("관리자 알림 발송 완료");
        } catch (Exception notificationException) {
            log.error("관리자 알림 발송 실패", notificationException);
        }

        // 실패를 상위로 전파
        throw new RuntimeException("S3 파일 업로드 최종 실패 : " + e.getMessage(), e );
    }

    @Override
    public InputStream get(UUID binaryContentId) {
        try {
            S3Client s3Client = getS3Client();
            String key = generateS3Key(binaryContentId);

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

            ResponseInputStream<GetObjectResponse> response = s3Client.getObject(getObjectRequest);

            log.info("✅ S3 다운로드 성공 - binaryContentId: {}, key: {}", binaryContentId, key);

            return response;
        } catch (Exception e) {
            log.error("❌ S3 다운로드 실패 - binaryContentId: {}, error: {}",
                binaryContentId, e.getMessage(), e);
            return new ByteArrayInputStream(new byte[0]);
        }
    }

    @Override
    public ResponseEntity<?> download(BinaryContentDto metaData) {
        try {
            String key = generateS3Key(metaData.id());
            String presignedUrl = generatePresignedUrl(key, metaData.contentType());

            log.info("PresignedURL 생성 성공 - binaryContentId: {}, filename: {}",
                metaData.id(), metaData.fileName());

            HttpHeaders headers = new HttpHeaders();
            headers.add("Location", presignedUrl);

            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        } catch (Exception e) {
            log.error("PresignedURL 생성 실패 - binaryContentId: {}, error: {}",
                metaData.id(), e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }

    private S3Client getS3Client() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        return S3Client.builder()
            .region(Region.of(region))
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .build();
    }

    private String generatePresignedUrl(String key, String contentType) {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        try (S3Presigner s3Presigner = S3Presigner.builder()
            .region(Region.of(region))
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .build()) {

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .responseContentType(contentType)
                .responseContentDisposition("attachment; filename=\"" + key.substring(key.lastIndexOf('/') + 1) + "\"")
                .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofSeconds(presignedUrlExpiration))
                .getObjectRequest(getObjectRequest)
                .build();

            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
            return presignedRequest.url().toString();
        }
    }

    private String generateS3Key(UUID binaryContentId) {
        String uuidStr = binaryContentId.toString();
        return String.format("binary-content/%s/%s/%s",
            uuidStr.substring(0, 2),
            uuidStr.substring(2, 4),
            uuidStr);
    }
}

