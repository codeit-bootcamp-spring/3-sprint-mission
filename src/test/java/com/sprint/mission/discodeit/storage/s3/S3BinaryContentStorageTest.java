package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

@DisplayName("S3BinaryContentStorageTest")
class S3BinaryContentStorageTest {

    private S3BinaryContentStorage binaryContentStorage;

    @BeforeEach
    void setUp() {
        binaryContentStorage = spy(new S3BinaryContentStorage("abcdefghi", "secretkey5oslr",
                "asia", "test-bucket"));
    }

    @Test
    @DisplayName("S3 업로드 시 S3Client.putObject를 호출하는지 테스트")
    void binaryContent_upload_test() {

        // given
        UUID id = UUID.randomUUID();
        byte[] bytes = "hello".getBytes();

        S3Client s3Client = mock(S3Client.class);
        willReturn(s3Client).given(binaryContentStorage).getS3Client();

        // when
        binaryContentStorage.put(id, bytes);

        // then
        ArgumentCaptor<PutObjectRequest> captor = ArgumentCaptor.forClass(PutObjectRequest.class);
        then(s3Client).should().putObject(captor.capture(), any(RequestBody.class));
        assertEquals("test-bucket", captor.getValue().bucket());
        assertEquals(id.toString(), captor.getValue().key());
    }

    @Test
    @DisplayName("S3 다운로드 테스트")
    void binaryContent_download_test() throws Exception {

        // given
        UUID id = UUID.randomUUID();
        String expectedUrl = "https://dummy-url.com/" + id;
        willReturn(expectedUrl).given(binaryContentStorage).generatePresignedUrl(any(),
                any());

        BinaryContentResponseDto binaryContentResponseDto = new BinaryContentResponseDto(
                id, "test.jpg", 3L, "image/png"
        );

        // when
        ResponseEntity<UrlResource> result = binaryContentStorage.download(binaryContentResponseDto);

        // then
        assertEquals(302, result.getStatusCode().value());
        assertEquals(expectedUrl, result.getHeaders().getLocation().toString());
    }
}