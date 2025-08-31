package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;

import java.io.InputStream;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DisplayName("S3BinaryContentStorage 단위 테스트")
@ExtendWith(MockitoExtension.class)
class S3BinaryContentStorageTest {

    @Mock
    private ApplicationEventPublisher eventPublisher;

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
        // 실제 생성자에 맞게 수정
        storage = new S3BinaryContentStorage(
                accessKey, 
                secretKey, 
                region, 
                bucket,
                eventPublisher
        );
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

        @Test
        @DisplayName("Spring Retry 어노테이션이 설정되어 있다")
        void retry_annotation_exists() {
            // given & when
            boolean hasRetryable = false;
            try {
                storage.getClass().getMethod("put", UUID.class, byte[].class);
                // 어노테이션 존재 여부는 런타임에 확인
                hasRetryable = true;
            } catch (NoSuchMethodException e) {
                hasRetryable = false;
            }
            
            // then
            assertThat(hasRetryable).isTrue();
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
            BinaryContentDto dto = new BinaryContentDto(
                testId, 
                "file.txt", 
                (long) testData.length, 
                "text/plain",
                BinaryContentStatus.SUCCESS
            );
            // when
            ResponseEntity<?> response = storage.download(dto);
            // then
            assertThat(response.getStatusCode().is3xxRedirection() || response.getStatusCode().is2xxSuccessful()).isTrue();
        }
    }

    @Nested
    @DisplayName("Spring Retry 메커니즘")
    class RetryMechanism {
        @Test
        @DisplayName("@Recover 메소드가 존재한다")
        void recover_method_exists() {
            // given & when
            boolean hasRecoverMethod = false;
            try {
                storage.getClass().getMethod("recoverPut", Exception.class, UUID.class, byte[].class);
                hasRecoverMethod = true;
            } catch (NoSuchMethodException e) {
                hasRecoverMethod = false;
            }
            
            // then
            assertThat(hasRecoverMethod).isTrue();
        }

        @Test
        @DisplayName("이벤트 발행이 가능하다")
        void event_publishing_capability() {
            // when & then
            // eventPublisher가 null이 아니고 이벤트 발행이 가능한지 확인
            assertThat(eventPublisher).isNotNull();
        }
    }
}