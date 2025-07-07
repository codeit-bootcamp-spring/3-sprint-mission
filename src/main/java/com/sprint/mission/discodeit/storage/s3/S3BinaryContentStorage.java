package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
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
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Log4j2
@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
public class S3BinaryContentStorage implements BinaryContentStorage {

    private final String accessKey;
    private final String secretKey;
    private final String region;
    private final String bucket;

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
    public UUID put(UUID binaryContentId, byte[] bytes) {

        try (S3Client s3Client = getS3Client()) {
            PutObjectResponse response = s3Client.putObject(
                PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(binaryContentId.toString())
                    .build(),
                RequestBody.fromBytes(bytes)
            );

            if (response.eTag() == null || response.eTag().isEmpty()) {
                throw new RuntimeException("Failed to upload file to S3");
            }
        }

        return binaryContentId;

    }

    @Override
    public InputStream get(UUID binaryContentId) {

        try {
            return getS3Client().getObject(GetObjectRequest.builder()
                .bucket(bucket)
                .key(binaryContentId.toString())
                .build());

        } catch (S3Exception s3e) {
            if ("NoSuchKey".equals(s3e.awsErrorDetails().errorCode())) {
                throw new NoSuchElementException(
                    "S3 object not found: " + binaryContentId, s3e);
            }
            throw s3e;
        }

    }

    @Override
    public ResponseEntity<Resource> download(BinaryContentDto metaData) {

        try (InputStream ignored = get(metaData.id())) {
            log.info("S3 filed exist - fileId: {}", metaData.id());
        } catch (IOException e) {
            throw new RuntimeException("Failed to verify S3 object", e);
        }

        String presignedUrl = generatePresignedUrl(metaData.id().toString(),
            metaData.contentType());

        log.info("S3 download - presignedUrl: {}", presignedUrl);

        return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT)
            .header(HttpHeaders.LOCATION, presignedUrl)
            .build();

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

        try (S3Presigner presigner = S3Presigner.builder()
            .region(Region.of(region))
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .build()) {

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofSeconds(600))
                .getObjectRequest(GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .responseContentType(contentType)
                    .build())
                .build();

            URI presignedUri = presigner.presignGetObject(presignRequest).url().toURI();
            return presignedUri.toString();

        } catch (Exception e) {
            throw new RuntimeException("Error generating presigned URL", e);
        }
    }

}
