package com.sprint.mission.discodeit.stoarge.s3;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Properties;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

public class AWSS3Test {

    static S3Client s3Client;
    static S3Presigner s3Presigner;
    static String bucket;
    static String accessKey;
    static String secretKey;
    static Region region;

    @BeforeAll
    static void setup() throws Exception {
        // .env -> Properties 로드
        Properties props = new Properties();
        props.load(new FileInputStream(".env"));

        // .env 파일에서 값 가져오기
        accessKey = props.getProperty("AWS_S3_ACCESS_KEY");
        secretKey = props.getProperty("AWS_S3_SECRET_KEY");
        bucket = props.getProperty("AWS_S3_BUCKET");
        region = Region.of(props.getProperty("AWS_S3_REGION"));

        // 자격증명 생성
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        // API 호출을 위한 인스턴스 생성
        s3Client = S3Client.builder()
            .region(region)
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .build();

        // URL 만들기 위한
        s3Presigner = S3Presigner.builder()
            .region(region)
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .build();
    }

    @Test
    void uploadTest() {
        PutObjectRequest request = PutObjectRequest.builder()
            .bucket(bucket)
            .key("test/upload.txt")
            .build();

        s3Client.putObject(request,
            RequestBody.fromFile(new File("src/test/resources/sample.txt")));
        System.out.println("Upload 성공");
    }

    @Test
    void downloadTest() throws IOException {
        Path downloadPath = Paths.get("downloaded.txt");

        // 이미 존재하면 삭제
        if (Files.exists(downloadPath)) {
            Files.delete(downloadPath);
        }

        GetObjectRequest request = GetObjectRequest.builder()
            .bucket(bucket)
            .key("test/upload.txt")
            .build();

        s3Client.getObject(request, ResponseTransformer.toFile(downloadPath));
        System.out.println("Download 완료");
    }

    @Test
    void presignedUrlTest() {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
            .bucket(bucket)
            .key("test/upload.txt")
            .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(10))
            .getObjectRequest(getObjectRequest)
            .build();

        URL url = s3Presigner.presignGetObject(presignRequest).url();
        System.out.println("URL: " + url);
    }
}