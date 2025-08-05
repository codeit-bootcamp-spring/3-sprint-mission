package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
@Slf4j
public class S3BinaryContentStorage implements BinaryContentStorage {

    private final S3Properties s3;

    private String accessKey;
    private String secretKey;
    private String region;
    private String bucket;

    @PostConstruct
    public void init() {
        this.region = s3.getRegion();
        this.accessKey = s3.getAccessKey();
        this.secretKey = s3.getSecretKey();
        this.bucket = s3.getBucket();
    }

    @Override
    public UUID put(UUID binaryContentId, byte[] bytes) {
        String key = "uploads/" + binaryContentId.toString();
        S3Client client = getS3Client();
        String contentType = detectContentType(bytes);

        log.info("[S3] put 요청: key={}, contentType={}, size={}", key, contentType, bytes.length);

        client.putObject(builder -> builder
                .bucket(bucket)
                .key(key)
                .contentLength((long) bytes.length)
                .contentType(contentType),
            RequestBody.fromBytes(bytes)
        );

        return binaryContentId;
    }

    @Override
    public InputStream get(UUID binaryContentId) {
        String key = "uploads/" + binaryContentId.toString();
        S3Client client = getS3Client();

        log.info("[S3] get 요청 : key={}", key);

        return client.getObject(builder -> builder
            .bucket(bucket)
            .key(key)
        );
    }

    @Override
    public ResponseEntity<Resource> download(BinaryContentDto binaryContentDto) {
        String key = "uploads/" + binaryContentDto.id().toString();
        String contentType = binaryContentDto.contentType();

        log.info("[S3] Presigned URL 생성 요청: key={}, contentType={}", key, contentType);

        // Content-Disposition 헤더를 포함한 PresignedUrl 생성
        try {
            String presignedUrl = generatePresignedUrl(key, contentType);
            log.info("Presigned URL 생성 성공: {}", presignedUrl);
            return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(presignedUrl))
                .build();
        } catch (Exception e) {
            log.error("Presigned URL 생성 실패: key={}", key, e);
            throw e;
        }
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
        int expiration = s3.getExpiration();
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        log.debug("[S3] Presigner 생성: expiration={}s, region={}", expiration, region);

        try (S3Presigner presigner = S3Presigner.builder()
            .region(Region.of(region))
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .build()) {

            // 확장자 추가
            String extension = getExtensionFromContentType(contentType);
            String fileName = key.substring(key.lastIndexOf('/') + 1) + extension;
            log.debug("Presign 대상 파일: fileName={}, extension={}", fileName, extension);

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .responseContentType(contentType)
                .responseContentDisposition("attachment; filename=\"" + fileName + "\"")
                .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofSeconds(expiration))
                .getObjectRequest(getObjectRequest)
                .build();

            return presigner.presignGetObject(presignRequest).url().toString();
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
            Tika tika = new Tika();
            return tika.detect(bytes);
        } catch (Exception e) {
            return "application/octet-stream";
        }
    }

}
