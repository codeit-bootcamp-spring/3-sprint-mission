package com.sprint.mission.discodeit.storage.s3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import io.github.cdimascio.dotenv.Dotenv;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class S3BinaryContentStorageTest {

    private final Logger log = LoggerFactory.getLogger(S3BinaryContentStorageTest.class);

    private S3BinaryContentStorage storage;
    // 테스트 key값 고정
    private UUID binaryContentId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private byte[] bytes;

    @BeforeAll
    void setUp() throws IOException {
        log.info("=== S3BinaryContentStorage 테스트 초기화 시작 ===");

        Dotenv dotenv = Dotenv.configure()
            .ignoreIfMissing()
            .load();

        String accessKey = Optional.ofNullable(System.getenv("AWS_ACCESS_KEY"))
            .orElse(dotenv.get("AWS_S3_ACCESS_KEY"));
        String secretKey = Optional.ofNullable(System.getenv("AWS_SECRET_KEY"))
            .orElse(dotenv.get("AWS_S3_SECRET_KEY"));
        String region = Optional.ofNullable(System.getenv("AWS_REGION"))
            .orElse(dotenv.get("AWS_S3_REGION"));
        String bucket = Optional.ofNullable(System.getenv("AWS_BUCKET"))
            .orElse(dotenv.get("AWS_S3_BUCKET"));

        String expirationStr = Optional.ofNullable(System.getenv("AWS_EXPIRATION"))
            .orElse(dotenv.get("AWS_S3_PRESIGNED_URL_EXPIRATION"));

        if (accessKey == null || secretKey == null || region == null || bucket == null) {
            throw new IllegalStateException("AWS 환경변수가 모두 설정되어야 합니다.");
        }

        S3Properties s3Properties = new S3Properties();
        s3Properties.setAccessKey(accessKey);
        s3Properties.setSecretKey(secretKey);
        s3Properties.setRegion(region);
        s3Properties.setBucket(bucket);
        if (expirationStr != null) {
            s3Properties.setExpiration(Integer.parseInt(expirationStr));
        }

        // S3BinaryContentStorage 초기화
        storage = new S3BinaryContentStorage(s3Properties);

        // 테스트 데이터 준비
        Resource resource = new ClassPathResource("test2.png");
        bytes = StreamUtils.copyToByteArray(resource.getInputStream());

        log.info("초기화 완료 - Access Key: {}, Region: {}, Bucket: {}",
            s3Properties.getAccessKey().substring(0, 4) + "***",
            s3Properties.getRegion(),
            s3Properties.getBucket());
    }

    @Test
    @Order(1)
    void put_uploadsFileToS3() {
        log.info("---  업로드 테스트 시작 ---");

        // When
        UUID result = storage.put(binaryContentId, bytes);

        // Then
        assertEquals(binaryContentId, result);
        log.info("업로드 성공 파일 ID: {}, 크기: {} bytes", result, bytes.length);
    }

    @Test
    @Order(2)
    void get() throws IOException {
        log.info("---  다운로드 테스트 시작 ---");

        // When
        try (InputStream inputStream = storage.get(binaryContentId)) {
            byte[] downloadedBytes = inputStream.readAllBytes();

            // Then
            assertEquals(bytes.length, downloadedBytes.length);
            log.info("이미지 다운로드 성공! 크기: {} bytes", downloadedBytes.length);
        }
    }

    @Test
    @Order(3)
    void download() {
        log.info("---  이미지 Presigned URL 리다이렉트 테스트 시작 ---");

        // Given
        BinaryContentDto binaryContentDto = new BinaryContentDto(
            binaryContentId,
            "test.png",
            (long) bytes.length,
            "image/png"
        );

        // When
        ResponseEntity<Resource> response = storage.download(binaryContentDto);

        // Then
        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertNotNull(response.getHeaders().getLocation());

        String redirectUrl = response.getHeaders().getLocation().toString();
        assertTrue(redirectUrl.contains("amazonaws.com"));
        assertTrue(redirectUrl.contains("response-content-disposition"));

        log.info("리다이렉트 응답 생성 성공!");
        log.info("리다이렉트 URL: {}", redirectUrl);
    }
}
