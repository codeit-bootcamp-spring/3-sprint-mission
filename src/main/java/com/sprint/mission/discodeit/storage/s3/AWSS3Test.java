package com.sprint.mission.discodeit.storage.s3;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.Properties;

public class AWSS3Test {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final String bucketName;

    public AWSS3Test() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(".env"));

        String accessKey = properties.getProperty("AWS_S3_ACCESS_KEY");
        String secretKey = properties.getProperty("AWS_S3_SECRET_KEY");
        String region = properties.getProperty("AWS_S3_REGION");
        this.bucketName = properties.getProperty("AWS_S3_BUCKET");

        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKey, secretKey);

        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();

        this.s3Presigner = S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
    }

    public void uploadTest(String key, String filePath) {
        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.putObject(putRequest, new File(filePath).toPath());
        System.out.println("Upload completed: " + key);
    }

    public void downloadTest(String key, String downloadPath) {
        GetObjectRequest getRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.getObject(getRequest, new File(downloadPath).toPath());
        System.out.println("Download completed: " + downloadPath);
    }

    public void presignedUrlTest(String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .getObjectRequest(getObjectRequest)
                .build();

        URL presignedUrl = s3Presigner.presignGetObject(presignRequest).url();
        System.out.println("presignedUrl.toString() = " + presignedUrl.toString());
    }

    public static void main(String[] args) throws IOException {

        AWSS3Test test = new AWSS3Test();

        test.uploadTest("test.jpg", "/Users/handongwoo/sprint-mission/3-sprint-mission/src/test.jpg");
        test.downloadTest("test.jpg", "/Users/handongwoo/sprint-mission/3-sprint-mission/src/downloaded.jpg");
        test.presignedUrlTest("test.jpg");
    }
}
