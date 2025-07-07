package com.sprint.mission.discodeit.storage.s3;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentDto;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@DisplayName("S3BinaryContentStorage 테스트")
@ExtendWith(MockitoExtension.class)
public class S3BinaryContentStorageTest {

    @Mock
    S3Client s3Client;

    @Mock
    S3Presigner s3Presigner;

    private S3BinaryContentStorage s3BinaryContentStorage = new S3BinaryContentStorage(
            "testAccessKey",
            "testSecretKey",
            "ap-northeast-2",
            "testBucket"
    );

    @BeforeEach
    void setUp() {

        // @Mock 객체 수동 주입
        ReflectionTestUtils.setField(s3BinaryContentStorage, "s3Client", s3Client);
        ReflectionTestUtils.setField(s3BinaryContentStorage, "s3Presigner", s3Presigner);
        ReflectionTestUtils.setField(s3BinaryContentStorage, "presignedUrlExpiration", 600);
    }

    @Test
    @DisplayName("유효한 저장 요청으로 데이터를 S3에 저장할 수 있다.")
    void shouldPutBinaryContent_whenValidRequest() {

        // given
        UUID id = UUID.randomUUID();
        byte[] content = "test".getBytes();

        PutObjectRequest expectedRequest = PutObjectRequest.builder()
                .bucket("testBucket")
                .key(id.toString())
                .build();

        given(s3Client.putObject(eq(expectedRequest), any(RequestBody.class))).willReturn(
                PutObjectResponse.builder().build());

        // when
        UUID result = s3BinaryContentStorage.put(id, content);

        // then
        assertThat(result).isEqualTo(id);
        verify(s3Client, times(1)).putObject(eq(expectedRequest), any(RequestBody.class));
    }

    @Test
    @DisplayName("유효한 조회 요청으로 파일의 InputStream을 반환할 수 있다.")
    void shouldGetInputStream_whenValidRequest() {

        // given
        UUID id = UUID.randomUUID();

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket("testBucket")
                .key(id.toString())
                .build();

        GetObjectResponse response = GetObjectResponse.builder().build();
        ResponseInputStream<GetObjectResponse> expectedInputStream = mock(
                ResponseInputStream.class);

        given(s3Client.getObject(any(GetObjectRequest.class))).willReturn(
                expectedInputStream);

        // when
        InputStream result = s3BinaryContentStorage.get(id);

        // then
        assertThat(result).isEqualTo(expectedInputStream);
        verify(s3Client, times(1)).getObject(any(GetObjectRequest.class));
    }

    @Test
    @DisplayName("유효한 다운로드 요청으로 파일 다운로드 리다이렉트 응답을 반환할 수 있다.")
    void shouldDownloadBinaryContent_whenValidRequest() {

        // given
        UUID id = UUID.randomUUID();
        BinaryContentDto metadata = new BinaryContentDto(id, "test.png", 1024L, "img/png");

        URI expectedUri = URI.create("http://testBucket/test.png");

        S3BinaryContentStorage spyStorage = spy(s3BinaryContentStorage);
        doReturn("http://testBucket/test.png")
                .when(spyStorage)
                .generatePresignedUrl(metadata.id().toString(), metadata.contentType());

        // when
        ResponseEntity<Resource> result = spyStorage.download(metadata);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(result.getHeaders().getLocation()).isEqualTo(expectedUri);
    }

    @Test
    @DisplayName("유효한 생성 요청으로 S3Client 인스턴스를 생성할 수 있다.")
    void shouldGetS3Client_whenValidRequest() {

        // when
        S3Client result = s3BinaryContentStorage.getS3Client();

        // then
        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(S3Client.class);
    }

    @Test
    @DisplayName("유효한 생성 요청으로 Presigned URL을 생성할 수 있다.")
    void shouldGeneratePresignedUrl_whenValidRequest() throws MalformedURLException {

        // given
        UUID id = UUID.randomUUID();
        String key = id.toString();
        String contentType = "image/png";
        String expectedPresignedUrl = "http://testBucket/" + key;

        URL mockUrl = new URL(expectedPresignedUrl);

        PresignedGetObjectRequest mockPresignedRequest = mock(PresignedGetObjectRequest.class);
        given(mockPresignedRequest.url()).willReturn(mockUrl);

        given(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class)))
                .willReturn(mockPresignedRequest);

        // when
        String result = s3BinaryContentStorage.generatePresignedUrl(key, contentType);

        // then
        assertThat(result).isEqualTo(expectedPresignedUrl);
    }
}
