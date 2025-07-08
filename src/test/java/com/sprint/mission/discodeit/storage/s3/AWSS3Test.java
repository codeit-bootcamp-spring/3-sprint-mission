package com.sprint.mission.discodeit.storage.s3;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Properties;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Disabled
public class AWSS3Test {

    private static S3Client s3Client;
    private static Region region;
    private static StaticCredentialsProvider credentialsProvider;
    private static String bucketName;

    @BeforeAll
    static void setup() throws IOException {
        Properties properties = new Properties();

        try (FileInputStream fis = new FileInputStream(".env")) {
            properties.load(fis);
        }

        String accessKey = properties.getProperty("AWS_S3_ACCESS_KEY");
        String secretKey = properties.getProperty("AWS_S3_SECRET_KEY");
        String regionName = properties.getProperty("AWS_S3_REGION");
        bucketName = properties.getProperty("AWS_S3_BUCKET");

        region = Region.of(regionName);
        credentialsProvider = StaticCredentialsProvider.create(
            AwsBasicCredentials.create(accessKey, secretKey)
        );

        s3Client = S3Client.builder()
            .region(region)
            .credentialsProvider(credentialsProvider)
            .build();
    }

    @Test
    void uploadTest() throws IOException {
        String key = "test-folder/test-upload.txt";

        File file = File.createTempFile("test-upload", ".txt");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write("Hello, S3 Upload Test!".getBytes());
        }

        PutObjectRequest request = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build();

        s3Client.putObject(request, RequestBody.fromFile(file));
        System.out.println("Upload successful: " + key);
    }

    @Test
    void downloadTest() throws IOException {
        String key = "test-folder/test-upload.txt";
        String downloadPath = System.getProperty("java.io.tmpdir") + "/test-download.txt";
        Path downloadFile = Paths.get(downloadPath);

        Files.deleteIfExists(downloadFile);

        GetObjectRequest request = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build();

        s3Client.getObject(request, downloadFile);
        System.out.println("Downloaded to: " + downloadPath);
    }


    @Test
    void presignedUrlTest() {
        String key = "test-folder/test-upload.txt";

        try (S3Presigner presigner = S3Presigner.builder()
            .region(region)
            .credentialsProvider(credentialsProvider)
            .build()) {

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .getObjectRequest(getObjectRequest)
                .build();

            URL presignedUrl = presigner.presignGetObject(presignRequest).url();
            System.out.println("Presigned URL: " + presignedUrl);
        }
    }
}
