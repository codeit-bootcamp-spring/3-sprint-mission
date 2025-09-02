package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.InputStream;
import java.time.Duration;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Slf4j
@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
public class S3BinaryContentStorage implements BinaryContentStorage {

    private final String accessKey;
    private final String secretKey;
    private final String region;
    private final String bucket;
    private final ApplicationEventPublisher eventPublisher;

    public S3BinaryContentStorage(@Value("${discodeit.storage.s3.access-key}") String accessKey,
        @Value("${discodeit.storage.s3.secret-key}") String secretKey,
        @Value("${discodeit.storage.s3.region}") String region,
        @Value("${discodeit.storage.s3.bucket}") String bucket,
        ApplicationEventPublisher eventPublisher) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.region = region;
        this.bucket = bucket;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Retryable(
        retryFor = {S3Exception.class, SdkClientException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 2000)
    )
    public UUID put(UUID id, byte[] bytes) {
        S3Client s3Client = getS3Client();
        String key = id.toString();

        PutObjectRequest putRequest = PutObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .build();

        s3Client.putObject(putRequest, RequestBody.fromBytes(bytes));

        return id;
    }

    @Override
    public InputStream get(UUID id) {
        S3Client s3Client = getS3Client();
        String key = id.toString();

        return s3Client.getObject(GetObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .build());
    }

    @Override
    public ResponseEntity<UrlResource> download(BinaryContentResponseDto binaryContentResponseDto) {
        String key = binaryContentResponseDto.id().toString();
        String presignedUrl = generatePresignedUrl(key, binaryContentResponseDto.contentType());

        try {
            UrlResource url = new UrlResource(presignedUrl);

            return ResponseEntity
                .status(302)
                .location(url.getURL().toURI())
                .build();

        } catch (Exception e) {
            throw new RuntimeException("Failed to create redirect for presigned URL", e);
        }
    }

    protected S3Client getS3Client() {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKey, secretKey);

        return S3Client.builder()
            .region(Region.of(region))
            .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
            .build();
    }

    String generatePresignedUrl(String key, String contentType) {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKey, secretKey);

        S3Presigner s3Presigner = S3Presigner.builder()
            .region(Region.of(region))
            .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
            .build();

        GetObjectRequest getRequest = GetObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .responseContentType(contentType)
            .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(10))
            .getObjectRequest(getRequest)
            .build();

        return s3Presigner.presignGetObject(presignRequest)
            .url()
            .toString();
    }

    @Recover
    public void recover(Exception e, UUID binaryContentId) {

        log.info("[S3BinaryContentStorage] 재시도 모두 실패 binaryContentId: {}", binaryContentId, e);

        S3UploadFailedEvent event = new S3UploadFailedEvent(MDC.get("requestId"), binaryContentId,
            e.getMessage());

        eventPublisher.publishEvent(event);
    }
}
