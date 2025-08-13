package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.InputStream;
import java.time.Duration;
import java.util.UUID;

/**
 * AWS S3를 활용하여 바이너리 콘텐츠를 저장/조회/다운로드하는 저장소 구현체.
 * 환경 변수로 설정값을 주입받아 S3 클라이언트를 구성한다.
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
public class S3BinaryContentStorage implements BinaryContentStorage {

    // AWS S3 인증 정보 및 설정값
    private final String accessKey;
    private final String secretKey;
    private final String region;
    private final String bucket;
    @Value("${discodeit.storage.s3.presigned-url-expiration:600}")
    public int presignedUrlExpiration;

    /**
     * 환경변수를 통해 설정값을 주입받아 Presigner를 초기화한다.
     * S3Client는 필요 시마다 생성하여 사용한다.
     */
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
     * 주어진 ID와 콘텐츠를 S3에 업로드한다.
     * @param binaryContentId 업로드할 객체의 UUID
     * @param bytes 저장할 파일의 바이너리 내용
     * @return 저장된 객체의 UUID
     */
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

            log.info("[S3BinaryContentStorage] Uploaded content successfully: {}, key: {}, size: {} bytes",
                    binaryContentId, key, bytes.length);

            return binaryContentId;
        } catch (Exception e) {
            log.error("[S3BinaryContentStorage] Failed to upload content - binaryContentId: {}, error: {}",
                    binaryContentId, e.getMessage(), e);
            throw new RuntimeException("S3 파일 업로드 실패", e);
        }
    }

    /**
     * 주어진 ID로 S3에서 객체를 조회하여 InputStream으로 반환한다.
     * @param binaryContentId 다운로드할 객체의 UUID
     * @return InputStream 형태의 객체
     * @throws BinaryContentNotFoundException 객체가 존재하지 않거나 S3 호출에 실패한 경우
     */
    @Override
    public InputStream get(UUID binaryContentId) {
        try {
            S3Client s3Client = getS3Client();
            String key = generateS3Key(binaryContentId);

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();
            log.info("[S3BinaryContentStorage] Fetching object.");
            ResponseInputStream<GetObjectResponse> response = s3Client.getObject(getObjectRequest);

            log.info("[S3BinaryContentStorage] Download succeeded - binaryContentId: {}", binaryContentId);

            return response;
        } catch (Exception e) {
            log.error("[S3BinaryContentStorage] Download failed - binaryContentId: {}",
                    binaryContentId, e);
            throw new BinaryContentNotFoundException("S3 객체 다운로드 실패: " + e);
        }
    }

    /**
     * Presigned URL을 생성하고, 이를 통해 다운로드 리다이렉트를 응답으로 반환한다.
     * @param metaData 다운로드 대상 정보
     * @return 302 리다이렉트 응답
     */
    @Override
    public ResponseEntity<Void> download(BinaryContentDto metaData) {
        try {
            String key = generateS3Key(metaData.id());
            String presignedUrl = generatePresignedUrl(key, metaData.contentType());

            log.info("[S3BinaryContentStorage] Generated PresignedURL - binaryContentId: {}, filename: {}",
                    metaData.id(), metaData.fileName());

            HttpHeaders headers = new HttpHeaders();
            headers.add("Location", presignedUrl);

            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        } catch (Exception e) {
            log.error("[S3BinaryContentStorage] Failed to generate PresignedURL - binaryContentId: {}",
                    metaData.id(), e);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 내부에서 사용할 S3Client를 생성한다.
     * @return 구성된 S3Client 인스턴스
     */
    private S3Client getS3Client() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

    /**
     * S3용 presigned URL을 생성한다.
     * @param key 객체 key
     * @param contentType 콘텐츠 타입
     * @return presigned URL 문자열
     */
    private String generatePresignedUrl(String key, String contentType) {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        try (S3Presigner s3Presigner = S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build())
        {
            String filename = key.contains("/") ? key.substring(key.lastIndexOf('/') + 1) : key;
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .responseContentType(contentType)
                    .responseContentDisposition("attachment: filename=\"" + filename + "\"")
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