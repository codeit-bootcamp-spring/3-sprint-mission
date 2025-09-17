package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.exception.binarycontent.FileSaveFailedException;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
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
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
@Slf4j
public class S3BinaryContentStorage implements BinaryContentStorage {

    private final S3Client s3Client;
    private final S3Properties props;

    private String accessKey;
    private String secretKey;
    private String region;
    private String bucket;

    @PostConstruct
    public void init() {
        this.region = props.getRegion();
        this.accessKey = props.getAccessKey();
        this.secretKey = props.getSecretKey();
        this.bucket = props.getBucket();
    }

    @Retryable(
        value = {S3Exception.class, IOException.class},
        maxAttempts = 3,               // 최대 3번 시도
        backoff = @Backoff(delay = 2000, multiplier = 2) // 2초 대기, 점진적으로 증가
    )
    @Override
    public UUID put(UUID binaryContentId, byte[] bytes) {
        try {
            String key = binaryContentId.toString();
            String contentType = detectContentType(bytes);

            log.info("[S3] put(bytes) 요청: key={}, contentType={}, size={}", key, contentType,
                bytes.length);

            s3Client.putObject(
                b -> b.bucket(bucket).key(key).contentType(contentType)
                    .contentLength((long) bytes.length),
                RequestBody.fromBytes(bytes)
            );
            return binaryContentId;
        } catch (Exception e) {
            log.error("[S3Storage] put 실패 id={}, ex={}", binaryContentId, e.toString());
            throw e;
        }
    }

    @Override
    public void put(String objectKey, InputStream in, long contentLength, String contentType) {
        try {
            PutObjectRequest req = PutObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .contentType(contentType)
                .contentLength(contentLength)
                .build();

            s3Client.putObject(req, RequestBody.fromInputStream(in, contentLength));
            log.info("[S3] put(stream) 성공: key={}, len={}", objectKey, contentLength);
        } catch (S3Exception | SdkClientException e) {
            log.error("[S3] put(stream) 실패: key={}, msg={}", objectKey, e.getMessage(), e);
            throw new FileSaveFailedException(objectKey, e);
        }
    }

    @Override
    public InputStream get(UUID binaryContentId) {
        String key = binaryContentId.toString();
        log.info("[S3] get 요청 : key={}", key);
        try {
            return s3Client.getObject(GetObjectRequest.builder().bucket(bucket).key(key).build());
        } catch (NoSuchKeyException e) {
            log.warn("[S3] get - 키 없음: {}", key);
            throw e;
        }
    }

    @Override
    public ResponseEntity<Resource> download(BinaryContentDto binaryContentDto) {
        String key = binaryContentDto.id().toString();
        String contentType = binaryContentDto.contentType();

        log.info("[S3] Presigned URL 생성 요청: key={}, contentType={}", key, contentType);
        String presignedUrl = generatePresignedUrl(key, contentType);
        log.info("[S3] Presigned URL 생성 성공: {}", presignedUrl);

        return ResponseEntity.status(HttpStatus.FOUND)
            .location(URI.create(presignedUrl))
            .build();
    }

    private S3Client getS3Client() {
        log.debug("[S3] S3Client 생성: region={}, accessKey=****", region);

        return S3Client.builder()
            .region(Region.of(region))
            .credentialsProvider(StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey, secretKey)
            ))
            .build();
    }

    private String generatePresignedUrl(String key, String contentType) {
        int expiration = props.getPresignedUrlExpiration();
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        try (S3Presigner presigner = S3Presigner.builder()
            .region(Region.of(region))
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .build()) {

            String fileName = key;
            String ext = getExtensionFromContentType(contentType);
            if (!ext.isEmpty()) {
                fileName += ext;
            }

            var getReq = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .responseContentType(contentType)
                .responseContentDisposition("attachment; filename=\"" + fileName + "\"")
                .build();

            var presign = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofSeconds(expiration))
                .getObjectRequest(getReq)
                .build();

            return presigner.presignGetObject(presign).url().toString();
        }
    }

    /**
     * ContentType에서 확장자 매핑
     */
    private String getExtensionFromContentType(String contentType) {
        if (contentType == null) {
            return "";
        }

        return switch (contentType.toLowerCase()) {
            case "image/jpeg", "image/jpg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/gif" -> ".gif";
            case "text/plain" -> ".txt";
            case "application/pdf" -> ".pdf";
            default -> "";
        };
    }


    private String detectContentType(byte[] bytes) {
        try {
            return new Tika().detect(bytes);
        } catch (Exception e) {
            return "application/octet-stream";
        }
    }

    @Recover
    public UUID recover(Exception e, UUID binaryContentId, byte[] bytes) {
        // 모든 재시도가 실패했을 때 호출됨
        String requestId = MDC.get("requestId");
        log.error("[S3Storage] put 실패 - id={}, requestId={}, cause={}",
            binaryContentId, requestId, e.toString(), e);

        // 관리자에게 알림 발생
        notifyAdmin(requestId, binaryContentId, e);

        throw new RuntimeException("S3 업로드 실패 -id=" + binaryContentId, e);
    }

    private void notifyAdmin(String requestId, UUID binaryContentId, Exception e) {
        String message = """
            S3 파일 업로드 실패
            RequestId: %s
            BinaryContentId: %s
            Error: %s
            """.formatted(requestId, binaryContentId, e.getMessage());

        log.warn("[ALERT] 관리자 알림 전송: {}", message);
    }
}
