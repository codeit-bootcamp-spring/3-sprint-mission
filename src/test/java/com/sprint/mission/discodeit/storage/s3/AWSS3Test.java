package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.util.EnvLoader;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Optional;
import java.util.Properties;

public class AWSS3Test {

    static S3Client s3;
    static S3Presigner presigner;
    static String bucket;

    @BeforeAll
    public static void setup() {
        // Load .env
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        String accessKey = Optional.ofNullable(System.getenv("AWS_ACCESS_KEY"))
                .orElse(dotenv.get("AWS_S3_ACCESS_KEY"));

        String secretKey = Optional.ofNullable(System.getenv("AWS_SECRET_KEY"))
                .orElse(dotenv.get("AWS_S3_SECRET_KEY"));

        String region = Optional.ofNullable(System.getenv("AWS_REGION"))
                .orElse(dotenv.get("AWS_S3_REGION"));

        bucket = Optional.ofNullable(System.getenv("AWS_BUCKET"))
                .orElse(dotenv.get("AWS_S3_BUCKET"));

        AwsBasicCredentials creds = AwsBasicCredentials.create(accessKey, secretKey);

        s3 = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(creds))
                .build();

        presigner = S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(creds))
                .build();
    }

    @Test
    public void uploadFile() {
        String key = "test/test-upload.txt";
        File file = new File("src/test/resources/sample.txt");

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        s3.putObject(request, file.toPath());

    }

    @Test
    public void generatePresignedUrl() {
        String key = "test/test-upload.txt";

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .getObjectRequest(getObjectRequest)
                .build();

        PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);

        URL url = presignedRequest.url();
    }

    @Test
    void downloadFile() throws IOException {
        // S3 내 저장된 경로
        String key = "test/test-upload.txt";

        // 다운로드 요청
        GetObjectRequest getRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        // 응답 스트림
        try (ResponseInputStream<GetObjectResponse> response = s3.getObject(getRequest);
             FileOutputStream output = new FileOutputStream("downloaded_" + Paths.get(key).getFileName())) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = response.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
        }
    }
}
