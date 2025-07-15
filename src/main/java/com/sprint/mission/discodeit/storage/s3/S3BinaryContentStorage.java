package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.jpa.JpaBinaryContentRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.InputStream;
import java.time.Duration;
import java.util.UUID;

/**
 * PackageName  : com.sprint.mission.discodeit.storage.s3
 * FileName     : S3BinaryContentStorage
 * Author       : dounguk
 * Date         : 2025. 7. 1.
 */
@Service
@ConditionalOnProperty(
    name = "discodeit.storage.type",
    havingValue = "aws",
    matchIfMissing = false
)
@RequiredArgsConstructor
public class S3BinaryContentStorage implements BinaryContentStorage {
    public final S3Values s3Values;
    public final JpaBinaryContentRepository binaryContentRepository;
    private static final Logger log = LoggerFactory.getLogger(S3BinaryContentStorage.class);

    @Override
    public UUID put(UUID binaryContentId, byte[] bytes) {
        log.info("upload profile image is {}",binaryContentId);
        //0+ new exception
        BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId).orElseThrow(() -> new IllegalStateException("image information is not saved"));

        try{
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(s3Values.getBucketName())
                .key(binaryContent.getFileName())
                .contentType(binaryContent.getContentType())
                .build();

            S3Client s3Client = getS3Client();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(bytes));
            log.info("Uploaded binary content with ID: {} to S3.", binaryContentId);
            return binaryContent.getId();

        } catch (S3Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public InputStream get(UUID binaryContentId) {
        BinaryContent attachment = binaryContentRepository.findById(binaryContentId).orElseThrow(() -> new IllegalStateException("image information not found"));
        try{
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(s3Values.getBucketName())
                .key(attachment.getFileName())
                .build();

            S3Client s3Client = getS3Client();

            log.info("InputStream for binary content with ID: {} from S3.", binaryContentId);
            return s3Client.getObject(getObjectRequest);

        } catch (S3Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<?> download(BinaryContentResponse response) {
        log.info("downloading image from S3: {}", response.fileName());

        String presignedUrl = generatedPresignedUrl(response.fileName(), response.contentType());

        return ResponseEntity.status(302)
            .header(HttpHeaders.LOCATION,presignedUrl)
            .build();
    }

    private S3Client getS3Client() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(s3Values.getAccessKey(), s3Values.getSecretKey());

        return S3Client.builder()
            .region(Region.of(s3Values.getRegion()))
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .build();
    }

    private String generatedPresignedUrl(String key, String contentType) {
        if (key.startsWith("env/")) {
            log.warn("'env/' 접근 시도 감지");
            throw new SecurityException("접근 불가능한 저장공간입니다.");
        }
        AwsBasicCredentials credentials = AwsBasicCredentials.create(s3Values.getAccessKey(), s3Values.getSecretKey());

        S3Presigner presigner = S3Presigner.builder()
            .region(Region.of(s3Values.getRegion()))
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .build();

        Duration expirationDuration = Duration.ofSeconds(s3Values.getPresignedUrlExpiration());

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
            .bucket(s3Values.getBucketName())
            .key(key)
            .build();

        PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(builder ->
            builder.signatureDuration(expirationDuration)
                .getObjectRequest(getObjectRequest));
        String url = presignedRequest.url().toString();

        presigner.close();
        return url;
    }

}
