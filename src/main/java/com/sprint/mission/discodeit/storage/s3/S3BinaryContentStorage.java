package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentDto;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@Slf4j
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
@Component
public class S3BinaryContentStorage implements BinaryContentStorage {

    private String accessKey;
    private String secretKey;
    private String region;
    private String bucket;

    @Value("${discodeit.storage.s3.presigned-url-expiration:600}")
    private int presignedUrlExpiration;

    private S3Client s3Client;
    private S3Presigner s3Presigner;

    public S3BinaryContentStorage(
            @Value("${discodeit.storage.s3.access-key}") String accessKey,
            @Value("${discodeit.storage.s3.secret-key}") String secretKey,
            @Value("${discodeit.storage.s3.region}") String region,
            @Value("${discodeit.storage.s3.bucket}") String bucket) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.region = region;
        this.bucket = bucket;
    }

    /**
     * S3Client, SePresigner 인스턴스 초기화
     */
    @PostConstruct
    public void init() {
        this.s3Client = getS3Client();
        this.s3Presigner = S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(s3Client.serviceClientConfiguration().credentialsProvider())
                .build();
    }

    /**
     * 지정된 id를 키로 하여 S3에 파일 업로드
     *
     * @param id    저장할 파일의 UUID
     * @param bytes 저장할 파일의 바이너리 데이터
     * @return 저장된 파일의 UUID
     */
    @Override
    public UUID put(UUID id, byte[] bytes) {
        log.info("S3_파일 저장 요청: ID = {}", id);

        String key = id.toString();

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        s3Client.putObject(request, RequestBody.fromBytes(bytes));
        return id;
    }

    /**
     * S3에서 지정된 ID의 파일을 조회하여 InputStream 반환
     *
     * @param id 조회할 파일의 UUID
     * @return 해당 파일의 InputStream
     */
    @Override
    public InputStream get(UUID id) {

        String key = id.toString();

        try {
            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            return s3Client.getObject(request);
        } catch (S3Exception e) {
            log.error("S3 객체 조회 실패: ID = {}", id);
            throw new RuntimeException("파일을 읽는 중 오류가 발생하였습니다.");
        }
    }

    /**
     * 파일 다운로드 리다이렉트 응답 반환 (Presigned URL 활용)
     *
     * @param metaData 다운로드할 파일의 메타데이터
     * @return 302 FOUND 응답과 Presigned URL 리다이렉트
     */
    @Override
    public ResponseEntity<Resource> download(BinaryContentDto metaData) {
        log.info("파일 다운로드 요청: ID = {}, 파일명 = {}, 형식 = {}", metaData.id(), metaData.fileName(),
                metaData.contentType());

        String url = generatePresignedUrl(metaData.id().toString(), metaData.contentType());
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(url));

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .headers(headers)
                .build();
    }

    /**
     * S3Client 인스턴스 생성
     *
     * @return S3Client 인스턴스
     */
    public S3Client getS3Client() {

        return S3Client.builder()
                .region(Region.of(region))
                .build();
    }

    /**
     * 지정된 키와 컨텐츠 타입으로 Presigned URL 생성
     *
     * @param key         객체 키
     * @param contentType 다운로드할 파일의 컨텐츠 타입
     * @return 생성된 Presigned URL
     */
    public String generatePresignedUrl(String key, String contentType) {

        String defaultContentType = "application/octet-stream";
        String resolvedContentType = contentType != null ? contentType : defaultContentType;

        String filename = extractFileNameWithExtension(key, resolvedContentType);

        GetObjectRequest objectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .responseContentType(resolvedContentType)
                .responseContentDisposition("attachment; filename=\"" + filename + "\"")
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(
                        Duration.ofMinutes(presignedUrlExpiration))
                .getObjectRequest(objectRequest)
                .build();

        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);

        return presignedRequest.url().toExternalForm();
    }

    private String extractFileNameWithExtension(String key, String contentType) {
        String baseName = key.substring(key.lastIndexOf("/") + 1);
        if (baseName.contains(".")) {
            return baseName;
        }

        String ext = switch (contentType) {
            case "image/jpeg" -> "jpg";
            case "image/png" -> "png";
            case "image/gif" -> "gif";

            default -> "jpg";
        };
        return baseName + "." + ext;
    }
}
