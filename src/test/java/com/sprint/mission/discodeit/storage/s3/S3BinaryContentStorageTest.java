package com.sprint.mission.discodeit.storage.s3;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.storage.S3BinaryContentStorage;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("S3BinaryContentStorage 통합 테스트")
class S3BinaryContentStorageTest {

    private static final Logger log = LoggerFactory.getLogger(S3BinaryContentStorageTest.class);
    private static final byte[] TEST_DATA = "test".getBytes();

    @Autowired
    private S3BinaryContentStorage s3BinaryContentStorage;

    @Value("${AWS_S3_BUCKET}")
    private String bucket;

    @Value("${AWS_S3_ACCESS_KEY}")
    private String accessKey;

    @Value("${AWS_S3_SECRET_KEY}")
    private String secretKey;

    @Value("${AWS_S3_REGION}")
    private String region;

    private UUID testId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
    }

    @AfterEach
    void cleanUp() {
        try (S3Client s3Client = buildS3Client()) {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(testId.toString())
                .build());
        }
    }

    private S3Client buildS3Client() {
        return S3Client.builder()
            .region(Region.of(region))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(accessKey, secretKey)
                )
            )
            .build();
    }

    @Test
    @DisplayName("S3에 파일 업로드 성공")
    void uploadFile() {
        UUID resultId = s3BinaryContentStorage.put(testId, TEST_DATA);
        assertThat(resultId).isEqualTo(testId);
    }

    @Test
    @DisplayName("S3에서 파일 다운로드 성공")
    void downloadFile() throws IOException {
        s3BinaryContentStorage.put(testId, TEST_DATA);

        try (InputStream input = s3BinaryContentStorage.get(testId)) {
            assertNotNull(input);
            byte[] downloaded = input.readAllBytes();
            assertThat(downloaded).isEqualTo(TEST_DATA);
        }
    }

    @Test
    @DisplayName("Presigned URL 생성 성공")
    void generatePresignedUrl() {
        s3BinaryContentStorage.put(testId, TEST_DATA);

        BinaryContentDto dto = new BinaryContentDto(
            testId,
            "test.txt",
            (long) TEST_DATA.length,
            "text/plain"
        );

        ResponseEntity<?> response = s3BinaryContentStorage.download(dto);

        assertThat(response.getStatusCodeValue()).isEqualTo(302);
        assertThat(response.getHeaders().getLocation()).isNotNull();

        String url = response.getHeaders().getLocation().toString();
        assertThat(url).contains(bucket).contains(testId.toString());

        log.info("생성된 Presigned URL: {}", url);
    }
}
