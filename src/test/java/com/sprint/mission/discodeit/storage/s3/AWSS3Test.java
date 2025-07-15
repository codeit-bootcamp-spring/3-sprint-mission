package com.sprint.mission.discodeit.storage.s3;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;
import java.util.Properties;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import io.github.cdimascio.dotenv.Dotenv;


public class AWSS3Test {

    private S3Client s3Client;
    private S3Presigner presigner;
    private String bucketName;

    @BeforeEach
    void setUp() throws IOException {

        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();

        String accessKey = Optional.ofNullable(System.getenv("AWS_ACCESS_KEY"))
                .orElse(dotenv.get("AWS_S3_ACCESS_KEY"));

        String secretKey = Optional.ofNullable(System.getenv("AWS_SECRET_KEY"))
                .orElse(dotenv.get("AWS_S3_SECRET_KEY"));

        String regionName = Optional.ofNullable(System.getenv("AWS_REGION"))
                .orElse(dotenv.get("AWS_S3_REGION"));

        bucketName = Optional.ofNullable(System.getenv("AWS_BUCKET"))
                .orElse(dotenv.get("AWS_S3_BUCKET"));

        if (accessKey == null || secretKey == null || regionName == null || bucketName == null) {
            throw new IllegalStateException("AWS 환경변수가 설정되지 않았습니다.");
        }

        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        Region region = Region.of(regionName);

        s3Client = S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(region)
                .build();

        presigner = S3Presigner.builder()
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(region)
                .build();
    }

    @Test
    void uploadTest() {
        String key = "test-folder/sample.txt";
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType("text/plain")
                .build();

        s3Client.putObject(request,
                RequestBody.fromString("Hello S3"));

        HeadObjectRequest headRequest = HeadObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        HeadObjectResponse headResponse = s3Client.headObject(headRequest);
        assertThat(headResponse.contentLength()).isGreaterThan(0);
    }

    @Test
    void downloadTest() {
        String key = "test-folder/sample.txt";
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        String content = s3Client.getObjectAsBytes(request)
                .asString(StandardCharsets.UTF_8);

        assertThat(content).contains("Hello S3");
    }

    @Test
    void generatePresignedUrlTest() {
        String key = "test-folder/sample.txt";
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .getObjectRequest(getObjectRequest)
                .build();

        URL url = presigner.presignGetObject(presignRequest).url();

        assertThat(url.toString()).contains(key);
        System.out.println("Presigned URL: " + url);
    }
}
