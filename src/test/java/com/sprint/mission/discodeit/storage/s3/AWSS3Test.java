package com.sprint.mission.discodeit.storage.s3;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;
import java.time.Duration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

public class AWSS3Test {
    private static final Properties props = new Properties();

    static {
        try (FileInputStream fis = new FileInputStream(".env")) {
            props.load(fis);
        } catch (IOException e) {
            throw new RuntimeException(".env 파일을 읽을 수 없습니다.", e);
        }
    }

    public static String getEnv(String key) {
        String env = System.getenv(key);
        return (env != null && !env.isEmpty()) ? env : props.getProperty(key);
    }

    public static S3Client s3() {
        return S3Client.builder()
            .region(Region.of(getEnv("AWS_S3_REGION")))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(
                        getEnv("AWS_ACCESS_KEY_ID"),
                        getEnv("AWS_SECRET_ACCESS_KEY")
                    )
                )
            )
            .build();
    }

    public static S3Presigner presigner() {
        return S3Presigner.builder()
            .region(Region.of(getEnv("AWS_S3_REGION")))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(
                        getEnv("AWS_ACCESS_KEY_ID"),
                        getEnv("AWS_SECRET_ACCESS_KEY")
                    )
                )
            )
            .build();
    }

    public static void testUpload() {
        S3Client s3 = s3();
        String bucket = getEnv("AWS_S3_BUCKET");
        String key = "test-upload.txt";
        String content = "Hello, S3! (업로드 테스트)";

        s3.putObject(
            PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build(),
            software.amazon.awssdk.core.sync.RequestBody.fromString(content)
        );
        System.out.println("업로드 완료! (" + bucket + "/" + key + ")");
    }

    public static void testDownload() {
        S3Client s3 = s3();
        String bucket = getEnv("AWS_S3_BUCKET");
        String key = "test-upload.txt";

        s3.getObject(
            GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build(),
            software.amazon.awssdk.core.sync.ResponseTransformer.toFile(Paths.get("downloaded.txt"))
        );
        System.out.println("다운로드 완료 (downloaded.txt)");
    }

    public static void testPresignedUrl() {
        S3Presigner presigner = presigner();
        String bucket = getEnv("AWS_S3_BUCKET");
        String key = "test-upload.txt";

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
            .getObjectRequest(getObjectRequest)
            .signatureDuration(Duration.ofMinutes(10))
            .build();

        String url = presigner.presignGetObject(presignRequest).url().toString();
        System.out.println("Presigned URL (10분 유효): " + url);
    }

    public static void main(String[] args) {
        System.out.println("[S3 업로드 테스트]");
        testUpload();

        System.out.println("[S3 다운로드 테스트]");
        testDownload();

        System.out.println("[Presigned URL 생성 테스트]");
        testPresignedUrl();
    }
}
