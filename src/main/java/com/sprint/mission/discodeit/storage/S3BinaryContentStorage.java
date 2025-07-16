package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.response.BinaryContentDto;
import jakarta.annotation.PreDestroy;
import java.io.InputStream;
import java.time.Duration;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.logging.Logger;
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
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3",  matchIfMissing = false)
public class S3BinaryContentStorage implements BinaryContentStorage{
    private static final Logger logger = Logger.getLogger(S3BinaryContentStorage.class.getName());

    private final String accessKey;
    private final String secretKey;
    private final String region;
    private final String bucket;
    private S3Client s3Client;

    @Value("${discodeit.storage.s3.presigned-url-expiration:600}")
    private int presignedUrlExpiration;

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

    @Override
    public UUID put(UUID id, byte[] bytes) {
        String key = id.toString();

        S3Client s3Client = getS3Client();

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .build(),
                RequestBody.fromBytes(bytes)
        );

        logger.info("S3에 파일 업로드 완료");

        return id;
    }

    @Override
    public InputStream get(UUID id) {
        S3Client s3Client = getS3Client();

        String key = id.toString();
        try {
            // GetObject 요청 생성
            GetObjectRequest getRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            logger.info("S3에서 파일 가져오기 완료");

            // S3에서 객체를 InputStream 으로 읽어옴
            return s3Client.getObject(getRequest);

        } catch (NoSuchKeyException e) {
            throw new NoSuchElementException(id + ", S3에 해당 파일이 존재하지 않습니다.");
        } catch (Exception e) {
            throw new RuntimeException("S3에서 파일을 가져오는 중 오류 발생", e);
        }
    }

    @Override
    public ResponseEntity<Void> download(BinaryContentDto binaryContentDto) {
        String key = binaryContentDto.id().toString();
        String contentType = binaryContentDto.contentType();

        // Presigned URL 생성
        String presignedUrl = generatePresignedUrl(key, contentType);

        return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT)
                .header(HttpHeaders.LOCATION, presignedUrl)
                .build();

    }

    @Override
    public void delete(UUID id) {
        String key = id.toString();

        S3Client s3Client = getS3Client();

        try {
            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            s3Client.deleteObject(request);

            logger.info("S3에서 파일 삭제");

        } catch (NoSuchKeyException e){
            logger.warning("파일이 존재하지 않습니다." + key);
        } catch (Exception e){
            throw new RuntimeException("S3에서 파일 삭제 중 오류 발생", e);
        }
    }

    public S3Client getS3Client(){
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        accessKey, secretKey
                                )
                        )
                )
                .build();
    }

    public String generatePresignedUrl(String key, String contentType){
        String presignedUrl;

        try (S3Presigner presigner = S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        accessKey, secretKey
                                )
                        )
                )
                .build()) {

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofSeconds(presignedUrlExpiration)) // 5분 동안 유효
                    .getObjectRequest(GetObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .responseContentType(contentType)
                            .build())
                    .build();

            presignedUrl = presigner.presignGetObject(presignRequest).url().toString();
        }
        logger.info("Presigned URL: " + presignedUrl);
        return presignedUrl;
    }

    @PreDestroy
    public void closeS3() {
        s3Client.close();
        logger.info("S3Client 리소스 정리");
    }
}
