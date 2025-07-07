package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.exception.binaryContent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.util.EnvLoader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.util.UUID;

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


    public S3BinaryContentStorage(
            @Value("${discodeit.storage.s3.access-key}") String accessKey,
            @Value("${discodeit.storage.s3.secret-key}") String secretKey,
            @Value("${discodeit.storage.s3.region}") String region,
            @Value("${discodeit.storage.s3.bucket}") String bucket,
            @Value("${discodeit.storage.s3.presigned-url-expiration:600}") int presignedUrlExpiration,
            BinaryContentRepository binaryContentRepository) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.region = region;
        this.bucket = bucket;
        this.presignedUrlExpiration = presignedUrlExpiration;

        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        this.s3 = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
        this.binaryContentRepository = binaryContentRepository;
    }

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
}
