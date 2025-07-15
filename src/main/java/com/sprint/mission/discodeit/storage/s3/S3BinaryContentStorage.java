package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
@Component
public class S3BinaryContentStorage implements BinaryContentStorage {

    private final String accessKey;
    private final String secretKey;
    private final String region;
    private final String bucket;

    private S3Presigner s3Presigner;
    private S3Client s3Client;

    @Value("${discodeit.storage.local.root-path}")
    private String path;

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


    @Override
    public UUID put(UUID binaryContentId, byte[] bytes) {
        try {
            String key = path + binaryContentId;

            PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentLength((long) bytes.length)
                .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .build();

            PutObjectResponse response = s3Client.putObject(putRequest,
                RequestBody.fromBytes(bytes));

            if (response.eTag() != null) {
                return binaryContentId;
            } else {
                throw new RuntimeException("Failed to upload file to S3");
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public InputStream get(UUID binaryContentId) {
        try {
            String key = path + binaryContentId;

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

            return s3Client.getObject(getObjectRequest);
        } catch (NoSuchKeyException e) {
            throw new RuntimeException("File with key " + binaryContentId + " does not exist");
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<?> download(BinaryContentDto metaData) {
        try {
            String url = generatePresignedUrl(metaData.id().toString(), metaData.contentType());

            return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(url))
                .build();

        } catch (NoSuchKeyException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostConstruct
    void init(){
        AwsBasicCredentials aws = AwsBasicCredentials.create(accessKey, secretKey);

        s3Client=S3Client.builder()
            .region(Region.of(region))
            .credentialsProvider(StaticCredentialsProvider.create(aws))
            .build();

        s3Presigner = S3Presigner.builder()
            .region(Region.of(region))
            .credentialsProvider(StaticCredentialsProvider.create(aws))
            .build();

    }

    @PreDestroy
    public void cleanup() {
        if (s3Client != null) {
            s3Client.close();
        }
        if (s3Presigner != null) {
            s3Presigner.close();
        }
    }

    private S3Client getS3Client(){
        return s3Client;
    }

    private String generatePresignedUrl(String key, String contentType){
        try{
            GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucket)
                .key(path+key)
                .responseContentType(contentType)
                .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(15))
                .getObjectRequest(request)
                .build();

            return s3Presigner.presignGetObject(presignRequest).url().toString();
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

}
