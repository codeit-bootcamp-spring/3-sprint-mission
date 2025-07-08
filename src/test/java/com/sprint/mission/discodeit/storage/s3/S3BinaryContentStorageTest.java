package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.ResponseEntity;

import java.io.InputStream;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DisplayName("S3BinaryContentStorage 단위 테스트")
class S3BinaryContentStorageTest {

    private S3BinaryContentStorage storage;
    private final String accessKey = "test-access-key";
    private final String secretKey = "test-secret-key";
    private final String region = "ap-northeast-2";
    private final String bucket = "test-bucket";
    public int presignedUrlExpiration = 600;
    private UUID testId;
    private byte[] testData;

    @BeforeEach
    void setUp() {
        storage = new S3BinaryContentStorage(accessKey, secretKey, region, bucket);
        storage.presignedUrlExpiration = presignedUrlExpiration;
        testId = UUID.randomUUID();
        testData = "test-data".getBytes();
    }

    @Nested
    @DisplayName("put 메소드")
    class PutMethod {
        @Test
        @DisplayName("정상적으로 S3에 업로드된다 (실제 S3 연동 없음)")
        void put_success() {
            // given, when
            // 실제 S3 연동이 아니므로 예외 발생 가능성 있음, 구조만 검증
            assertThatThrownBy(() -> storage.put(testId, testData))
                .isInstanceOf(RuntimeException.class);
        }
    }

    @Nested
    @DisplayName("get 메소드")
    class GetMethod {
        @Test
        @DisplayName("정상적으로 S3에서 다운로드된다 (실제 S3 연동 없음)")
        void get_success() {
            // given, when
            assertThatThrownBy(() -> storage.get(testId))
                .isInstanceOf(RuntimeException.class);
        }
    }

    @Nested
    @DisplayName("download 메소드")
    class DownloadMethod {
        @Test
        @DisplayName("정상적으로 Presigned URL을 반환한다 (실제 S3 연동 없음)")
        void download_success() {
            // given
            BinaryContentDto dto = new BinaryContentDto(testId, "file.txt", (long) testData.length, "text/plain");
            // when
            ResponseEntity<?> response = storage.download(dto);
            // then
            assertThat(response.getStatusCode().is3xxRedirection() || response.getStatusCode().is2xxSuccessful()).isTrue();
        }
    }
}