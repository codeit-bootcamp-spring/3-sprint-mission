package com.sprint.mission.discodeit.storage.s3;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;
import java.time.Duration;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;


public class S3BinaryContentStorage implements BinaryContentStorage {
    private final String accessKey;
    private final String secretKey;
    private final String region;
    private final String bucket;
    private final Duration presignedUrlExpiration;

    public S3BinaryContentStorage(
        String accessKey,
        String secretKey,
        String region,
        String bucket,
        Duration presignedUrlExpiration
    ) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.region = region;
        this.bucket = bucket;
        this.presignedUrlExpiration = presignedUrlExpiration;
    }


    public S3Client getS3Client() {
        return S3Client.builder()
            .region(Region.of(region))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(accessKey, secretKey)
                )
            )
            .build();
    }

    public S3Presigner getS3Presigner() {
        return S3Presigner.builder()
            .region(Region.of(region))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(accessKey, secretKey)
                )
            )
            .build();
    }

    @Override
    public UUID put(UUID uuid, byte[] bytes) {
        S3Client s3 = getS3Client();
        String key = uuid.toString();

        s3.putObject(
            PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build(),
            software.amazon.awssdk.core.sync.RequestBody.fromBytes(bytes)
        );
        return uuid;
    }

    @Override
    public InputStream get(UUID uuid) {
        S3Client s3 = getS3Client();
        String key = uuid.toString();

        ResponseBytes<GetObjectResponse> objectBytes = s3.getObject(
            GetObjectRequest.builder().bucket(bucket).key(key).build(),
            software.amazon.awssdk.core.sync.ResponseTransformer.toBytes()
        );
        return new ByteArrayInputStream(objectBytes.asByteArray());
    }
    @Override
    public ResponseEntity<Void> download(BinaryContentDto dto) {
        String key = dto.id().toString();
        String url = generatePresignedUrl(key, dto.contentType());
        return ResponseEntity.status(302).header("Location", url).build();
    }

    private String generatePresignedUrl(String key, String contentType) {
        S3Presigner presigner = getS3Presigner();
        GetObjectRequest getReq = GetObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .responseContentType(contentType)
            .build();
        GetObjectPresignRequest preReq = GetObjectPresignRequest.builder()
            .getObjectRequest(getReq)
            .signatureDuration(presignedUrlExpiration)
            .build();
        return presigner.presignGetObject(preReq).url().toString();
    }

}
