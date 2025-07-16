package com.sprint.mission.discodeit.storage.s3;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.UUID;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@ExtendWith(MockitoExtension.class)
class S3BinaryContentStorageTest {

    private static final String ACCESS_KEY = "DUMMY_ACCESS_KEY";
    private static final String SECRET_KEY = "DUMMY_SECRET_KEY";
    private static final String REGION = "ap-northeast-2";
    private static final String BUCKET = "dummy-bucket";
    private static final Duration EXPIRE = Duration.ofMinutes(10);
    private static final UUID TEST_UUID = UUID.randomUUID();
    private static final String TEST_CONTENT_TYPE = "text/plain";
    private static final String TEST_FILE_NAME = "test.txt";
    private static final long TEST_FILE_SIZE = 5L;
    private static final String PRESIGNED_URL = "https://example.com/" + TEST_UUID;

    @Mock
    private S3Client s3Client;

    @Mock
    private S3Presigner s3Presigner;

    private TestableStorage storage;

    @BeforeEach
    void setUp() {
        storage = new TestableStorage();
    }

    @DisplayName("put() 호출 시 S3Client.putObject 가 올바르게 호출된다")
    @Test
    void put_calls_putObject() {
        // when
        storage.put(TEST_UUID, "hello".getBytes());

        // then
        verify(s3Client).putObject(
            argThat((PutObjectRequest req) ->
                BUCKET.equals(req.bucket()) && TEST_UUID.toString().equals(req.key())
            ),
            any(software.amazon.awssdk.core.sync.RequestBody.class)
        );
    }

    @DisplayName("download()는 302 Location 헤더에 Presigned URL을 담아 반환한다")
    @Test
    void download_returns_redirect() throws MalformedURLException{
        // given
        TestableStorage storage = new TestableStorage();

        // PresignedGetObjectRequest mock 생성 및 URL 세팅
        PresignedGetObjectRequest presignedRequest = mock(PresignedGetObjectRequest.class);
        when(presignedRequest.url()).thenReturn(new URL(PRESIGNED_URL));

        // s3Presigner mock이 presignGetObject 호출시 위 presignedRequest 반환하도록 지정
        when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class))).thenReturn(presignedRequest);

        BinaryContentDto dto = new BinaryContentDto(
            TEST_UUID,
            TEST_FILE_NAME,
            TEST_FILE_SIZE,
            TEST_CONTENT_TYPE
        );

        // when
        ResponseEntity<Void> response = storage.download(dto);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getFirst("Location")).isEqualTo(PRESIGNED_URL);
    }


    /**
     * S3BinaryContentStorage를 테스트하기 위한 간단한 래퍼.
     * getS3Client(), getS3Presigner()만 mock 인스턴스로 대체.
     */
    private class TestableStorage extends S3BinaryContentStorage {
        TestableStorage() {
            super(ACCESS_KEY, SECRET_KEY, REGION, BUCKET, EXPIRE);
        }
        @Override public S3Client getS3Client() { return s3Client; }
        @Override public S3Presigner getS3Presigner() { return s3Presigner; }
    }
}
