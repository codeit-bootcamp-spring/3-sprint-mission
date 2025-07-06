package com.sprint.mission.discodeit.storage;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.mission.discodeit.entity.BinaryContent;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

@ExtendWith(MockitoExtension.class)
class S3BinaryContentStorageTest {

  @Mock
  private S3Client s3Client;

  @Mock
  private S3Presigner s3Presigner;

  @Mock
  private ResponseInputStream<GetObjectResponse> responseInputStream;

  @Mock
  private PresignedGetObjectRequest presignedGetObjectRequest;

  private S3BinaryContentStorage storage;
  private final String testAccessKey = "test-access-key";
  private final String testSecretKey = "test-secret-key";
  private final String testRegion = "ap-northeast-2";
  private final String testBucket = "test-bucket";
  private final long testExpiration = 600L;

  @BeforeEach
  void setUp() {
    // 테스트용 생성자로 Mock 객체들을 주입
    storage = new S3BinaryContentStorage(testAccessKey, testSecretKey, testRegion, testBucket,
        testExpiration, s3Client, s3Presigner);
  }

  @Test
  void put_파일_업로드_성공() {
    UUID testId = UUID.randomUUID();
    byte[] testContent = "test content".getBytes();

    when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
        .thenReturn(PutObjectResponse.builder().build());

    UUID result = storage.put(testId, testContent);

    assertEquals(testId, result);
    verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
  }

  @Test
  void put_파일_업로드_실패시_예외_발생() {
    UUID testId = UUID.randomUUID();
    byte[] testContent = "test content".getBytes();

    when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
        .thenThrow(new RuntimeException("S3 오류"));

    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
      storage.put(testId, testContent);
    });

    assertTrue(exception.getMessage().contains("S3 파일 업로드 실패"));
  }

  @Test
  void get_파일_조회_성공() throws IOException {
    UUID testId = UUID.randomUUID();
    byte[] testContent = "test content".getBytes();

    when(s3Client.getObject(any(GetObjectRequest.class)))
        .thenReturn(responseInputStream);
    when(responseInputStream.readAllBytes())
        .thenReturn(testContent);

    InputStream result = storage.get(testId);

    assertNotNull(result);
    byte[] resultBytes = result.readAllBytes();
    assertArrayEquals(testContent, resultBytes);

    verify(s3Client, times(1)).getObject(any(GetObjectRequest.class));
    verify(responseInputStream, times(1)).readAllBytes();
    verify(responseInputStream, times(1)).close();
  }

  @Test
  void get_파일_조회_실패시_예외_발생() throws IOException {
    UUID testId = UUID.randomUUID();

    when(s3Client.getObject(any(GetObjectRequest.class)))
        .thenReturn(responseInputStream);
    when(responseInputStream.readAllBytes())
        .thenThrow(new IOException("읽기 오류"));

    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
      storage.get(testId);
    });

    assertTrue(exception.getMessage().contains("S3 파일 조회 실패"));
  }

  @Test
  void download_Presigned_URL로_리다이렉트() throws Exception {
    BinaryContent binaryContent = mock(BinaryContent.class);
    UUID testId = UUID.randomUUID();
    String testContentType = "image/jpeg";
    String testPresignedUrl = "https://test-bucket.s3.ap-northeast-2.amazonaws.com/test-key?presigned=true";

    when(binaryContent.getId()).thenReturn(testId);
    when(binaryContent.getContentType()).thenReturn(testContentType);
    when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class)))
        .thenReturn(presignedGetObjectRequest);
    when(presignedGetObjectRequest.url())
        .thenReturn(new java.net.URL(testPresignedUrl));

    ResponseEntity<Resource> response = storage.download(binaryContent);

    assertEquals(HttpStatus.FOUND, response.getStatusCode());
    assertEquals(testPresignedUrl, response.getHeaders().getFirst(HttpHeaders.LOCATION));
    verify(s3Presigner, times(1)).presignGetObject(any(GetObjectPresignRequest.class));
  }

  @Test
  void download_실패시_500_에러_반환() {
    BinaryContent binaryContent = mock(BinaryContent.class);
    UUID testId = UUID.randomUUID();

    when(binaryContent.getId()).thenReturn(testId);
    when(binaryContent.getContentType()).thenReturn("image/jpeg");
    when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class)))
        .thenThrow(new RuntimeException("Presigner 오류"));

    ResponseEntity<Resource> response = storage.download(binaryContent);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
  }

  @Test
  void generatePresignedUrl_URL_생성_성공() throws Exception {
    String testKey = "test-key";
    String testContentType = "image/jpeg";
    String expectedUrl = "https://test-bucket.s3.ap-northeast-2.amazonaws.com/test-key?presigned=true";

    when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class)))
        .thenReturn(presignedGetObjectRequest);
    when(presignedGetObjectRequest.url())
        .thenReturn(new java.net.URL(expectedUrl));

    String result = storage.generatePresignedUrl(testKey, testContentType);

    assertEquals(expectedUrl, result);
    verify(s3Presigner, times(1)).presignGetObject(any(GetObjectPresignRequest.class));
  }

  @Test
  void getS3Client_클라이언트_반환() {
    S3Client result = storage.getS3Client();

    assertEquals(s3Client, result);
  }
}
