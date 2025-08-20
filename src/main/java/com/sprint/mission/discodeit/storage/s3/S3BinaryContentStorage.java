package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.InputStream;
import java.time.Duration;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
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
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@Component
@ConditionalOnProperty(prefix = "discodeit.storage", name = "type", havingValue = "s3")
public class S3BinaryContentStorage implements BinaryContentStorage {
    private final S3Client s3Client;
    private final String accessKey;
    private final String secretKey;
    private final String region;
    private final String bucket;
    private final Duration expiration;

    public S3BinaryContentStorage(AwsProperties aws) {
        this.accessKey = aws.accessKey();
        this.secretKey = aws.secretKey();
        this.region = aws.region();
        this.bucket = aws.bucket();
        this.expiration = Duration.ofSeconds(aws.presignedUrlExpiration());
        this.s3Client = getS3Client();
    }

    @Override
    public UUID put(UUID id, byte[] data) {
        PutObjectRequest putRequest = PutObjectRequest.builder()
            .bucket(bucket)
            .key(id.toString())
            .build();

        s3Client.putObject(putRequest, RequestBody.fromBytes(data));
        return id;
    }

    @Override
    public InputStream get(UUID id) {
        GetObjectRequest getRequest = GetObjectRequest.builder()
            .bucket(bucket)
            .key(id.toString())
            .build();

        return s3Client.getObject(getRequest);
    }

    @Override
    public ResponseEntity<Void> download(BinaryContentDto dto) {
        String presignedUrl = generatePresignedUrl(
            dto.id().toString() , dto.contentType(), dto.fileName()
        );

        return ResponseEntity
            .status(HttpStatus.SEE_OTHER) // 302
            .header(HttpHeaders.LOCATION, presignedUrl)
            .build();
    }

    public S3Client getS3Client() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        return S3Client.builder()
            .region(Region.of(region))
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .build();
    }

    public String generatePresignedUrl(String key, String contentType, String filename) {
        S3Presigner presigner = S3Presigner.builder()
            .region(Region.of(region))
            .credentialsProvider(StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey, secretKey)))
            .build();

        GetObjectRequest getReq = GetObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .responseContentType(contentType)
            .responseContentDisposition("attachment; filename=\"" + filename + "\"")
            .build();

        GetObjectPresignRequest presignReq = GetObjectPresignRequest.builder()
            .signatureDuration(expiration)
            .getObjectRequest(getReq)
            .build();

        PresignedGetObjectRequest pre = presigner.presignGetObject(presignReq);
        presigner.close();

        return pre.url().toString();
    }
}