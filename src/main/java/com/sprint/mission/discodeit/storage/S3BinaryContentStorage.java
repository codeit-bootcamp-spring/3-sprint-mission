package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Slf4j
@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
@ConfigurationProperties(prefix = "discodeit.storage.aws")
@Getter
@Setter
public class S3BinaryContentStorage implements BinaryContentStorage {

  private String accessKey;
  private String secretAccessKey;
  private String s3Bucket;
  private String s3Region;
  private String s3PresignedUrlExpiration;

  public S3BinaryContentStorage() {
  }

  @PostConstruct
  public void init() {
    log.info("accessKey = {}", accessKey);
    log.info("secretAccessKey {}", secretAccessKey);
    log.info("s3Bucket {}", s3Bucket);
    log.info("s3Region {}", s3Region);
  }

  //XXX: presignedUrl로 클라이언트에서 바로 AWS에 파일 get,put할수있는데, 이 메서드가 왜 필요하지?,
  // put은 서버에서 presignedUrl 없이 직접 파일 넣어주고, download만 presignedUrl 사용하는건가?
  @Override
  public UUID put(UUID binaryContentId, byte[] bytes) {
    try {
      PutObjectRequest putObjectRequest = PutObjectRequest.builder()
          .bucket(this.s3Bucket)
          .key(binaryContentId.toString())
          .build();

      PutObjectResponse response = this.getS3Client()
          .putObject(putObjectRequest, RequestBody.fromBytes(bytes));

      if (response.sdkHttpResponse().isSuccessful() &&
          response.eTag() != null) {
        log.info("S3 Object Upload Success");
      } else {
        //TODO : 새로운 에러타입 만들어서 넣을것
        throw new RuntimeException();
      }

    } catch (Exception e) {
      //TODO : 새로운 에러타입 만들어서 넣을것
      throw new RuntimeException(e);
    }

    return binaryContentId;
  }

  //XXX. deprecated. 대신 download() 메서드 사용함
  @Override
  public InputStream get(UUID binaryContentId) {
    return null;
  }

  @Override
  public ResponseEntity<Resource> download(BinaryContentDto binaryContent) {
    HttpHeaders httpHeaders = new HttpHeaders();
    try {
      URI presignedUrl = new URI(
          this.generatePresignedUrl(binaryContent.fileName(), binaryContent.contentType(),
              SdkHttpMethod.GET));
      httpHeaders.setLocation(presignedUrl);
      return new ResponseEntity<>(httpHeaders, HttpStatus.PERMANENT_REDIRECT);
    } catch (URISyntaxException e) {
      // TODO: error handler 내부 처리
      throw new RuntimeException(e);
    }
  }


  private String generatePresignedUrl(String key, String contentType, SdkHttpMethod method) {
    if (method == SdkHttpMethod.GET) {
      return generateGetPresignedUrl(key);
    } else if (method == SdkHttpMethod.PUT) {
      return generatePutPresignedUrl(key);
    } else {
      // TODO: error handler 내부 처리
      throw new UnsupportedOperationException("Unsupported HTTP method: " + method);
    }
  }

  private String generatePutPresignedUrl(String key) {
    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
        .bucket(s3Bucket)
        .key(key)
        .build();

    PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
        .signatureDuration(Duration.ofSeconds(Long.parseLong(s3PresignedUrlExpiration)))
        .putObjectRequest(putObjectRequest)
        .build();

    PresignedPutObjectRequest presignedRequest = s3Presigner().presignPutObject(presignRequest);
    return presignedRequest.url().toString();
  }

  private String generateGetPresignedUrl(String key) {

    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
        .bucket(s3Bucket)
        .key(key)
        .build();

    // you can change expiration time here
    GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
        .signatureDuration(Duration.ofMinutes(60))
        .getObjectRequest(getObjectRequest)
        .build();

    PresignedGetObjectRequest presignedRequest = s3Presigner().presignGetObject(presignRequest);
    return presignedRequest.url().toString();
  }

  //AWS S3 버킷에 파일 업로드/다운로드, 목록 조회, 삭제 등 일반적인 API 요청을 보낼 때 사용
  // 즉, 서버가 직접 S3에 접근해야 할 때 사용
  private S3Client getS3Client() {
    try {
      Region awsRegion = Region.of(this.s3Region);
      AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(this.accessKey,
          this.secretAccessKey);
      return S3Client.builder()
          .region(awsRegion)
          .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
          .build();
    } catch (Exception e) {
      // TODO: error 타입 생성 및 처리
      throw new RuntimeException(e);
    }

  }

  private S3Presigner s3Presigner() {
    try {
      Region awsRegion = Region.of(this.s3Region);
      return S3Presigner.builder()
          .region(awsRegion)
          .credentialsProvider(StaticCredentialsProvider.create(
              AwsBasicCredentials.create(this.accessKey, this.secretAccessKey)))
          .build();
    } catch (Exception e) {
      // TODO: error 타입 생성 및 처리
      throw new RuntimeException(e);
    }

  }

}
