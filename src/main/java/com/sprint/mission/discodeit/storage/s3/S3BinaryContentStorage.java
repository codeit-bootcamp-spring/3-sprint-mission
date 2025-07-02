package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
public class S3BinaryContentStorage implements BinaryContentStorage {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final String bucket;
    private final Duration presignedUrlExpiration;

    public S3BinaryContentStorage(
        @Value("${discodeit.storage.s3.access-key}") String accessKey,
        @Value("${discodeit.storage.s3.secret-key}") String secretKey,
        @Value("${discodeit.storage.s3.region}") String region,
        @Value("${discodeit.storage.s3.bucket}") String bucket,
        @Value("${discodeit.storage.s3.presigned-url-expiration:600}") long expirationSeconds
    ) {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        this.s3Client = S3Client.builder()
            .region(Region.of(region))
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .build();

        this.s3Presigner = S3Presigner.builder()
            .region(Region.of(region))
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .build();

        this.bucket = bucket;
        this.presignedUrlExpiration = Duration.ofSeconds(expirationSeconds);
    }

    @Override
    public UUID put(UUID uuid, byte[] content, String contentType) {

        PutObjectRequest request = PutObjectRequest.builder()
            .bucket(bucket)
            .key(uuid.toString())
            .contentType(contentType)
            .build();

        s3Client.putObject(request, RequestBody.fromBytes(content));
        return uuid;
    }

    @Override
    public InputStream get(UUID uuid) {
        GetObjectRequest request = GetObjectRequest.builder()
            .bucket(bucket)
            .key(uuid.toString())
            .build();

        return s3Client.getObject(request);
    }

    @Override
    public ResponseEntity<?> download(BinaryContentDto dto) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
            .bucket(bucket)
            .key(dto.id().toString())
            .responseContentDisposition("attachment; filename=\"" + dto.fileName() + "\"")
            .responseContentType(dto.contentType())
            .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
            .signatureDuration(presignedUrlExpiration)
            .getObjectRequest(getObjectRequest)
            .build();

        URL url = s3Presigner.presignGetObject(presignRequest).url();

        return ResponseEntity.status(302)
            .location(URI.create(url.toString()))
            .build();
    }
}