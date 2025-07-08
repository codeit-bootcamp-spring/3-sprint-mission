package com.sprint.mission.discodeit.storage.s3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Disabled
public class AWSS3Test {

    private S3Client s3Client;
    private S3Presigner s3Presigner;
    private String accessKey;
    private String secretKey;
    private String bucketName;
    private String region;
    private final String fileName = ".env";


    @BeforeEach
    void setUp() {
        loadProperties();
        InitializeS3();
    }

    private void loadProperties() {
        Properties properties = new Properties();

        try (FileInputStream input = new FileInputStream(fileName)) {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.accessKey = properties.getProperty("AWS_S3_ACCESS_KEY");
        this.secretKey = properties.getProperty("AWS_S3_SECRET_KEY");
        this.bucketName = properties.getProperty("AWS_S3_BUCKET");
        this.region = properties.getProperty("AWS_S3_REGION");

        assertNotNull(accessKey, "AWS_ACCESS_KEY_ID는 필수입니다.");
        assertNotNull(secretKey, "AWS_SECRET_ACCESS_KEY는 필수입니다.");
        assertNotNull(bucketName, "AWS_S3_BUCKET_NAME은 필수입니다.");
        assertNotNull(region, "AWS_S3_REGION은 필수입니다.");
    }

    private void InitializeS3(){
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKey, secretKey);

        this.s3Client = S3Client.builder()
            .region(Region.of(region))
            .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
            .build();

        this.s3Presigner = S3Presigner.builder()
            .region(Region.of(region))
            .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
            .build();
    }

    @Test
    @DisplayName("S3 파일 업로드 테스트")
    void uploadTest(){
        //Given
        String testKey = "test-files/upload-test.txt";
        String testContent = "테스트 파일 내용입니다. 현재 시간: " + System.currentTimeMillis();

        //When
        PutObjectRequest putRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(testKey)
            .contentType("text/plain")
            .build();

        PutObjectResponse response = s3Client.putObject(
            putRequest,
            RequestBody.fromString(testContent)
        );

        //Then
        assertNotNull(response);
        assertNotNull(response.eTag());

        HeadObjectRequest headRequest = HeadObjectRequest.builder()
            .bucket(bucketName)
            .key(testKey)
            .build();

        HeadObjectResponse headResponse = s3Client.headObject(headRequest);
        assertTrue(headResponse.contentLength() > 0);

    }

    @Test
    @DisplayName("S3 파일 다운로드 테스트")
    void testDownloadFile() throws IOException {
        // Given - 먼저 테스트 파일 업로드
        String testKey = "test-files/download-test.txt";
        String originalContent = "다운로드 테스트용 파일 내용";

        s3Client.putObject(
            PutObjectRequest.builder()
                .bucket(bucketName)
                .key(testKey)
                .build(),
            RequestBody.fromString(originalContent)
        );

        // When - 파일 다운로드
        GetObjectRequest getRequest = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(testKey)
            .build();

        String downloadedContent = s3Client.getObjectAsBytes(getRequest).asUtf8String();

        // Then
        assertEquals(originalContent, downloadedContent);
        System.out.println("다운로드 성공 - 내용: " + downloadedContent);
    }

    @Test
    @DisplayName("S3 업로드용 Presigned URL 생성 테스트")
    void GenerateUploadPresignedUrlTest(){
        // Given - 먼저 테스트 파일 업로드
        String testKey = "test-files/presigned-download-test.txt";
        String testContent = "Presigned URL 다운로드 테스트";

        s3Client.putObject(
            PutObjectRequest.builder()
                .bucket(bucketName)
                .key(testKey)
                .build(),
            RequestBody.fromString(testContent)
        );

        Duration expiration = Duration.ofHours(1);

        // When
        GetObjectRequest getRequest = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(testKey)
            .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
            .signatureDuration(expiration)
            .getObjectRequest(getRequest)
            .build();

        String presignedUrl = s3Presigner.presignGetObject(presignRequest).url().toString();

        // Then
        assertNotNull(presignedUrl);
        assertTrue(presignedUrl.contains(bucketName));
        assertTrue(presignedUrl.contains(testKey));
        assertTrue(presignedUrl.contains("X-Amz-Signature"));

        System.out.println("다운로드용 Presigned URL 생성 성공:");
        System.out.println(presignedUrl);
        System.out.println("만료 시간: " + expiration.toHours() + "시간");
    }
}
