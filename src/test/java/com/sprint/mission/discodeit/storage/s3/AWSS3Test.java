package com.sprint.mission.discodeit.storage.s3;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Properties;

import org.junit.jupiter.api.*;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AWSS3Test {

    private S3Client s3;
    private S3Presigner presigner;
    private String bucket;

    private static final String KEY = "sampleTest-ljy.txt";

    /**
     * .env 로드 및 클라이언트 생성
     */
    @BeforeAll
    void setUp() throws IOException {
        Properties env = new Properties();
        env.load(Files.newBufferedReader(Path.of(".env")));

        AwsBasicCredentials credentials = AwsBasicCredentials.create(
            env.getProperty("AWS_S3_ACCESS_KEY"),
            env.getProperty("AWS_S3_SECRET_KEY"));

        Region region = Region.of(env.getProperty("AWS_S3_REGION"));
        this.bucket = env.getProperty("AWS_S3_BUCKET");

        this.s3 = S3Client.builder()
            .region(region)
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .build();

        this.presigner = S3Presigner.builder()
            .region(region)
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .build();
    }

    /**
     * 리소스 정리
     */
    @AfterAll
    void tearDown() {
        if (s3 != null) {
            s3.close();
        }
        if (presigner != null) {
            presigner.close();
        }
    }

    /**
     * 1) 업로드
     */
    @Test
    @DisplayName("S3 파일 업로드 성공")
    void upload() throws IOException {
        Path tmp = Files.writeString(Files.createTempFile("s3-upload", ".txt"),
            "JUnit5 S3 upload test");

        PutObjectResponse response = s3.putObject(
            PutObjectRequest.builder()
                .bucket(bucket)
                .key(KEY)
                .contentType("text/plain")
                .build(),
            RequestBody.fromFile(tmp));

        assertThat(response.eTag()).isNotBlank();
        Files.deleteIfExists(tmp);
    }

    /**
     * 2) 다운로드
     */
    @Test
    @DisplayName("S3 파일 다운로드 성공")
    void download() throws IOException {
        Path target = Files.createTempFile("s3-download", ".txt");

        try (ResponseInputStream<GetObjectResponse> obj =
            s3.getObject(GetObjectRequest.builder()
                .bucket(bucket)
                .key(KEY)
                .build())) {
            obj.transferTo(Files.newOutputStream(target));
        }

        String body = Files.readString(target);
        System.out.println("target = " + target);
        assertThat(body).contains("JUnit5 S3 upload test");
        Files.deleteIfExists(target);
    }

    /**
     * 3) Presigned URL 생성
     */
    @Test
    @DisplayName("Presigned URL 생성 성공")
    void presignedUrl() throws URISyntaxException {
        URI url = presigner.presignGetObject(
                GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10))
                    .getObjectRequest(
                        GetObjectRequest.builder()
                            .bucket(bucket)
                            .key(KEY)
                            .build())
                    .build())
            .url().toURI();

        System.out.println("Presigned URL: " + url);
        assertThat(url.toString()).startsWith("https://");
    }
}
