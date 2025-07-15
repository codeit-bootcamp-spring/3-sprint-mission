package com.sprint.mission.discodeit.storage.s3;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.Fail.fail;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class S3BinaryContentStorageTest {

    private S3BinaryContentStorage storage;
    private UUID testId;
    private byte[] testData;

    @BeforeAll
    void setUp() throws IOException {
        Properties env = new Properties();
        env.load(Files.newBufferedReader(Path.of(".env")));

        storage = new S3BinaryContentStorage(
            env.getProperty("AWS_S3_ACCESS_KEY"),
            env.getProperty("AWS_S3_SECRET_KEY"),
            env.getProperty("AWS_S3_REGION"),
            env.getProperty("AWS_S3_BUCKET")
        );

        testId = UUID.randomUUID();
        testData = "S3BinaryContentStorage 테스트 데이터".getBytes();
    }

    @Test
    @DisplayName("파일 업로드 성공")
    void put_success() {
        UUID result = storage.put(testId, testData);
        System.out.println("upload file id = " + result);
        assertThat(result).isEqualTo(testId);
    }

    @Test
    @DisplayName("파일 조회 성공")
    void get_success() {
        UUID testId = UUID.randomUUID();
        storage.put(testId, testData);

        try (InputStream inputStream = storage.get(testId)) {
            byte[] retrievedData = inputStream.readAllBytes();
            assertThat(retrievedData).isEqualTo(testData);

        } catch (IOException e) {
            fail("파일 조회 중 예외 발생: " + e.getMessage(), e);
        }
    }

    @Test
    @DisplayName("다운로드 - PresignedUrl 리다이렉트")
    void download_success() {
        UUID testId = UUID.randomUUID();
        storage.put(testId, testData);

        BinaryContentDto binaryContentDto = new BinaryContentDto(
            testId,
            "test-file.txt",
            (long) testData.length,
            "text/plain"
        );

        ResponseEntity<Resource> response = storage.download(binaryContentDto);

        System.out.println(
            "response.getHeaders().getLocation() = " + response.getHeaders().getLocation());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.TEMPORARY_REDIRECT);
        assertThat(response.getHeaders().getLocation()).isNotNull();

    }

}
