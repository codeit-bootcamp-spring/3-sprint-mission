package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.exception.binaryContent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.RetryException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

@Slf4j
@Configuration
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
public class S3BinaryContentStorage implements BinaryContentStorage {

    private final S3Client s3;
    private final String bucket;
    private final String region;
    private final String accessKey;
    private final String secretKey;
    private final int presignedUrlExpiration;
    private final BinaryContentRepository binaryContentRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;


    public S3BinaryContentStorage(
            @Value("${discodeit.storage.s3.access-key}") String accessKey,
            @Value("${discodeit.storage.s3.secret-key}") String secretKey,
            @Value("${discodeit.storage.s3.region}") String region,
            @Value("${discodeit.storage.s3.bucket}") String bucket,
            @Value("${discodeit.storage.s3.presigned-url-expiration:600}") int presignedUrlExpiration,
            BinaryContentRepository binaryContentRepository, NotificationRepository notificationRepository, UserRepository userRepository, ApplicationEventPublisher eventPublisher) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.region = region;
        this.bucket = bucket;
        this.presignedUrlExpiration = presignedUrlExpiration;
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;

        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        this.s3 = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
        this.binaryContentRepository = binaryContentRepository;
    }

    @Retryable(
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000),
            retryFor = { RetryException.class, TimeoutException.class}
    )
    @Override
    public UUID put(UUID id, byte[] bytes) {
        String contentType = binaryContentRepository.findById(id).orElseThrow(() -> new BinaryContentNotFoundException(id)).getContentType();
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(id.toString())
                .contentType(contentType)
                .build();

        s3.putObject(request, RequestBody.fromBytes(bytes));
        return id;
    }

    @Override
    public InputStream get(UUID id) {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucket)
                .key(id.toString())
                .build();

        return s3.getObject(request);
    }

    @Override
    public ResponseEntity<?> download(BinaryContentDto binaryContent) {
        try {
            String key = binaryContent.id().toString();

            // Presigner 생성
            S3Presigner presigner = S3Presigner.builder()
                    .region(Region.of(region))
                    .credentialsProvider(
                            StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey))
                    )
                    .build();

            // Content-Disposition 추가
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .responseContentDisposition("attachment; filename=\"" + binaryContent.fileName() + "\"")
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .getObjectRequest(getObjectRequest)
                    .signatureDuration(Duration.ofSeconds(presignedUrlExpiration))
                    .build();

            String url = presigner.presignGetObject(presignRequest).url().toString();

            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create(url));
            return new ResponseEntity<>(headers, HttpStatus.FOUND);

        } catch (NoSuchKeyException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("파일을 찾을 수 없습니다: " + binaryContent.id());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Presigned URL 생성 실패: " + e.getMessage());
        }
    }

    @Recover
    private void recover(RetryException e, UUID id) {
        log.info("S3 업로드 재시도 전체 실패.");

        List<Notification> notifications = userRepository.findByRole(Role.ADMIN).stream()
                .map(user -> {
                    return new Notification(
                            "S3 파일 업로드 실패",
                            "RequestsId: " + MDC.get("requestId") + "BinaryContentId: " + id + "Error: " + e.getMessage(),
                            user
                    );
                })
                .toList();

        notificationRepository.saveAll(notifications);
    }

    @Recover
    private void recover(TimeoutException e, UUID id) {
        log.info("S3 업로드 재시도 전체 실패.");

        List<User> users = userRepository.findByRole(Role.ADMIN);

        S3UploadFailedEvent event = new S3UploadFailedEvent(
                MDC.get("requestId"),
                id,
                e.getMessage(),
                users
        );
        eventPublisher.publishEvent(event);
    }
}
