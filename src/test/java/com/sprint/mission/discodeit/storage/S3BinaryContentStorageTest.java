package com.sprint.mission.discodeit.storage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.dto.response.BinaryContentDto;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.http.AbortableInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

@ExtendWith(MockitoExtension.class)
@DisplayName("S3BinaryContentStorage 테스트")
public class S3BinaryContentStorageTest {

    private S3BinaryContentStorage storage;
    private S3Client mockS3Client;
    private UUID testId;
    private byte[] testByte;

    @BeforeEach
    void setUp() {
        mockS3Client = mock(S3Client.class);

        // spy로 감싸서 getS3Client()를 오버라이드할 수 있게 함
        storage = spy(new S3BinaryContentStorage(
                "mock-access",
                "mock-secret",
                "ap-northeast-2",
                "test-bucket"
        ));

        testId = UUID.randomUUID();
        testByte = "S3BinaryContentStorage 테스트 데이터".getBytes();
    }

    @Test
    @DisplayName("파일 업로드 성공 - S3에 파일 업로드 후 반환하는 UUID 비교")
    void testPutSuccess(){
        doReturn(mockS3Client).when(storage).getS3Client();

        UUID result = storage.put(testId, testByte);
        assertThat(result).isEqualTo(testId);
    }

    @Test
    @DisplayName("파일 불러오기 성공 - S3에서 파일 다운로드 성공 시 InputStream 반환")
    void testGetSuccess(){
        doReturn(mockS3Client).when(storage).getS3Client();

        InputStream inputStream = new ByteArrayInputStream(testByte);
        ResponseInputStream<GetObjectResponse> mockResponse = new ResponseInputStream<>(GetObjectResponse.builder().build(), AbortableInputStream.create(inputStream));

        given(mockS3Client.getObject(any(GetObjectRequest.class)))
                .willReturn(mockResponse);

        try (InputStream result = storage.get(testId)) {
            assertThat(result.readAllBytes()).isEqualTo(testByte);
        } catch (Exception e) {
            fail("예외 발생: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("파일 다운로드 성공 - presigned URL 생성 후 307 리다이렉트 응답 (TEMPORARY_REDIRECT) 반환")
    void testDownload(){
        BinaryContentDto binaryContentDto = new BinaryContentDto(
                testId,
                "test-file.png",
                1000L,
                "image/png"
        );

        String mockPresignedUrl = "https://mock-presigned-url.com/download";

        doReturn(mockPresignedUrl)
                .when(storage)
                .generatePresignedUrl(testId.toString(), "image/png");

        ResponseEntity<Void> response = storage.download(binaryContentDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.TEMPORARY_REDIRECT);
        assertThat(Objects.requireNonNull(response.getHeaders().getLocation()).toString()).isEqualTo(mockPresignedUrl);

    }

    @Test
    @DisplayName("파일 삭제 성공")
    void deleteTest(){
        doReturn(mockS3Client).when(storage).getS3Client();

        storage.delete(testId);

        verify(mockS3Client).deleteObject(argThat( (DeleteObjectRequest test) ->
                test.bucket().equals("test-bucket") && test.key().equals(testId.toString())
        ));
    }
}
