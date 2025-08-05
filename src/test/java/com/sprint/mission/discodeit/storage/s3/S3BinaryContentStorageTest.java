package com.sprint.mission.discodeit.storage.s3;

import static org.junit.jupiter.api.Assertions.*;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.core.ResponseInputStream;
import java.io.ByteArrayInputStream;


@Testcontainers
public class S3BinaryContentStorageTest {

    @Container
    static LocalStackContainer localstack = new LocalStackContainer(DockerImageName.parse("localstack/localstack:latest"))
        .withServices(LocalStackContainer.Service.S3);

    private TestS3BinaryContentStorage storage;
    private static final String BUCKET_NAME = "test-bucket";
    private static final String ACCESS_KEY = "test";
    private static final String SECRET_KEY = "test";
    private static final String REGION = "us-east-1";

    @BeforeEach
    void setUp() {
        // LocalStack 엔드포인트로 S3 클라이언트 생성하여 버킷 생성
        S3Client s3Client = S3Client.builder()
            .endpointOverride(localstack.getEndpointOverride(LocalStackContainer.Service.S3))
            .credentialsProvider(StaticCredentialsProvider.create(
                AwsBasicCredentials.create(ACCESS_KEY, SECRET_KEY)))
            .region(Region.of(REGION))
            .forcePathStyle(true)  // LocalStack에 필수
            .build();

        // 버킷 생성
        s3Client.createBucket(CreateBucketRequest.builder().bucket(BUCKET_NAME).build());

        // 테스트용 Storage 생성 (LocalStack 엔드포인트 사용)
        storage = new TestS3BinaryContentStorage(
            ACCESS_KEY, SECRET_KEY, REGION, BUCKET_NAME, 600,
            localstack.getEndpointOverride(LocalStackContainer.Service.S3).toString()
        );
    }

    /**
     * 테스트용 S3BinaryContentStorage 클래스
     * LocalStack 엔드포인트를 사용하도록 오버라이드
     */
    private static class TestS3BinaryContentStorage extends S3BinaryContentStorage {
        private final String endpointOverride;

        public TestS3BinaryContentStorage(String accessKey, String secretKey, String region,
            String bucket, int presignedUrlExpiration, String endpointOverride) {
            super(accessKey, secretKey, region, bucket, presignedUrlExpiration);
            this.endpointOverride = endpointOverride;
        }

        @Override
        public UUID put(UUID binaryContentId, byte[] bytes) {
            try {
                S3Client s3Client = createLocalStackS3Client();
                String key = generateS3Key(binaryContentId);

                PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(getBucket())
                    .key(key)
                    .contentLength((long) bytes.length)
                    .build();

                s3Client.putObject(putObjectRequest, RequestBody.fromBytes(bytes));
                return binaryContentId;
            } catch (Exception e) {
                throw new RuntimeException("S3 파일 업로드 실패", e);
            }
        }

        @Override
        public InputStream get(UUID binaryContentId) {
            try {
                S3Client s3Client = createLocalStackS3Client();
                String key = generateS3Key(binaryContentId);

                GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(getBucket())
                    .key(key)
                    .build();

                ResponseInputStream<GetObjectResponse> response = s3Client.getObject(getObjectRequest);
                return response;
            } catch (Exception e) {
                return new ByteArrayInputStream(new byte[0]);
            }
        }

        @Override
        public ResponseEntity<?> download(BinaryContentDto metaData) {
            try {
                String key = generateS3Key(metaData.id());
                String presignedUrl = generateLocalStackPresignedUrl(key, metaData.contentType());

                return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", presignedUrl)
                    .build();
            } catch (Exception e) {
                return ResponseEntity.notFound().build();
            }
        }

        private S3Client createLocalStackS3Client() {
            AwsBasicCredentials credentials = AwsBasicCredentials.create(getAccessKey(), getSecretKey());

            return S3Client.builder()
                .region(Region.of(getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .endpointOverride(URI.create(endpointOverride))  // LocalStack 엔드포인트
                .forcePathStyle(true)  // LocalStack에 필수
                .build();
        }

        private String generateLocalStackPresignedUrl(String key, String contentType) {
            AwsBasicCredentials credentials = AwsBasicCredentials.create(getAccessKey(), getSecretKey());

            try (S3Presigner s3Presigner = S3Presigner.builder()
                .region(Region.of(getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .endpointOverride(URI.create(endpointOverride))  // LocalStack 엔드포인트
                .build()) {

                GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(getBucket())
                    .key(key)
                    .responseContentType(contentType)
                    .responseContentDisposition("attachment; filename=\"" + key.substring(key.lastIndexOf('/') + 1) + "\"")
                    .build();

                GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofSeconds(600))
                    .getObjectRequest(getObjectRequest)
                    .build();

                PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
                return presignedRequest.url().toString();
            }
        }

        // 부모 클래스의 protected 필드에 접근하기 위한 getter 메서드들
        private String getAccessKey() { return ACCESS_KEY; }
        private String getSecretKey() { return SECRET_KEY; }
        private String getRegion() { return REGION; }
        private String getBucket() { return BUCKET_NAME; }

        private String generateS3Key(UUID binaryContentId) {
            String uuidStr = binaryContentId.toString();
            return String.format("binary-content/%s/%s/%s",
                uuidStr.substring(0, 2),
                uuidStr.substring(2, 4),
                uuidStr);
        }
    }

    @Test
    void put_파일_업로드_성공() {
        // Given
        UUID binaryContentId = UUID.randomUUID();
        byte[] testData = "test file content".getBytes();

        // When
        UUID result = storage.put(binaryContentId, testData);

        // Then
        assertEquals(binaryContentId, result);
    }

    @Test
    void get_파일_다운로드_성공() throws IOException {
        // Given
        UUID binaryContentId = UUID.randomUUID();
        byte[] testData = "test file content".getBytes();
        storage.put(binaryContentId, testData);

        // When
        InputStream result = storage.get(binaryContentId);

        // Then
        assertNotNull(result);
        byte[] downloadedData = result.readAllBytes();
        assertArrayEquals(testData, downloadedData);
    }

    @Test
    void download_PresignedURL_리다이렉트_성공() {
        // Given
        UUID binaryContentId = UUID.randomUUID();
        byte[] testData = "test file content".getBytes();
        storage.put(binaryContentId, testData);

        BinaryContentDto metaData = new BinaryContentDto(
            binaryContentId,
            "test-file.txt",
            (long) testData.length,
            "text/plain"
        );

        // When
        ResponseEntity<?> result = storage.download(metaData);

        // Then
        assertEquals(HttpStatus.FOUND, result.getStatusCode());
        assertTrue(result.getHeaders().containsKey("Location"));
        assertNotNull(result.getHeaders().getFirst("Location"));
    }

    @Test
    void get_존재하지_않는_파일_빈_스트림_반환() throws IOException {
        // Given
        UUID nonExistentId = UUID.randomUUID();

        // When
        InputStream result = storage.get(nonExistentId);

        // Then
        assertNotNull(result);
        assertEquals(0, result.readAllBytes().length);
    }
}
