package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.config.AWSS3Properties;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@Slf4j
@Component
@RequiredArgsConstructor
public class AWSS3Test {

    private final AWSS3Properties awsS3Properties;
    private S3Client s3Client;
    private S3Presigner s3Presigner;

    @PostConstruct
    public void init() {
        /* AWS 자격 증명 설정 */
        AwsBasicCredentials credentials = AwsBasicCredentials.create(
            awsS3Properties.getAccessKey(),
            awsS3Properties.getSecretKey()
        );

        /* S3 클라이언트 초기화 */
        s3Client = S3Client.builder()
            .region(Region.of(awsS3Properties.getRegion()))
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .build();

        /* S3 Presigner 초기화 */
        s3Presigner = S3Presigner.builder()
            .region(Region.of(awsS3Properties.getRegion()))
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .build();

        log.info("AWS S3 Client 초기화 완료 - bucket: {}, region: {}", awsS3Properties.getBucket(), awsS3Properties.getRegion());
    }

    /* 이미지 파일 업로드 테스트 */
    public void testImageUpload() {
        try {
            /* resources에서 이미지 파일 읽기 */
            ClassPathResource resource = new ClassPathResource("화난 강아지.jpg");

            if (!resource.exists()) {
                log.error("이미지 파일을 찾을 수 없습니다: 화난 강아지.jpg");
                return;
            }

            String key = "test/images/angry-dog-" + System.currentTimeMillis() + ".jpg";

            /* 파일을 바이트 배열로 읽기 */
            byte[] imageBytes;
            try (InputStream inputStream = resource.getInputStream()) {
                imageBytes = inputStream.readAllBytes();
            }

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(awsS3Properties.getBucket())
                .key(key)
                .contentType("image/jpeg")
                .contentLength((long) imageBytes.length)
                .build();

            PutObjectResponse response = s3Client.putObject(
                putObjectRequest,
                RequestBody.fromBytes(imageBytes)
            );

            log.info("이미지 업로드 성공!");
            log.info("   - Key: {}", key);
            log.info("   - Size: {} bytes", imageBytes.length);
            log.info("   - ETag: {}", response.eTag());
            log.info("   - Content-Type: image/jpeg");

        } catch (IOException e) {
            log.error("이미지 파일 읽기 실패: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("이미지 업로드 실패: {}", e.getMessage(), e);
        }
    }

    /* 업로드된 이미지 확인 테스트 */
    public void testImageDownload() {
        try {
            /* 최근에 업로드된 이미지 키 찾기 ( 간단하게 고정된 키 사용 ) */
            String key = "test/images/angry-dog-latest.jpg";

            /* 먼저 최신 이미지를 고정 키로 업로드 */
            testImageUploadWithFixedKey();

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(awsS3Properties.getBucket())
                .key(key)
                .build();

            ResponseInputStream<GetObjectResponse> response = s3Client.getObject(getObjectRequest);

            byte[] downloadedBytes = response.readAllBytes();
            GetObjectResponse metadata = response.response();
            response.close();

            log.info("이미지 다운로드 성공!");
            log.info("   - Key: {}", key);
            log.info("   - Size: {} bytes", downloadedBytes.length);
            log.info("   - Content-Type: {}", metadata.contentType());
            log.info("   - Last-Modified: {}", metadata.lastModified());

        } catch (Exception e) {
            log.error("이미지 다운로드 실패: {}", e.getMessage(), e);
        }
    }

    /* 이미지 PresignedURL 생성 테스트 */
    public void testImagePresignedUrl() {
        try {
            String key = "test/images/angry-dog-latest.jpg";

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(awsS3Properties.getBucket())
                .key(key)
                .responseContentType("image/jpeg")
                .responseContentDisposition("inline; filename=\"angry-dog.jpg\"")
                .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .getObjectRequest(getObjectRequest)
                .build();

            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
            URL presignedUrl = presignedRequest.url();

            log.info("이미지 PresignedURL 생성 성공!");
            log.info("   - Key: {}", key);
            log.info("   - URL: {}", presignedUrl);
            log.info("   - 만료시간: 10분");
            log.info("   - 브라우저에서 URL을 열어 이미지를 확인하세요!");

        } catch (Exception e) {
            log.error("이미지 PresignedURL 생성 실패: {}", e.getMessage(), e);
        }
    }

    /* 고정 키로 이미지 업로드 (다운로드 테스트용) */
    private void testImageUploadWithFixedKey() {
        try {
            ClassPathResource resource = new ClassPathResource("화난 강아지.jpg");
            String key = "test/images/angry-dog-latest.jpg";

            byte[] imageBytes;
            try (InputStream inputStream = resource.getInputStream()) {
                imageBytes = inputStream.readAllBytes();
            }

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(awsS3Properties.getBucket())
                .key(key)
                .contentType("image/jpeg")
                .contentLength((long) imageBytes.length)
                .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(imageBytes));
            log.info("고정 키로 이미지 업로드 완료: {}", key);

        } catch (Exception e) {
            log.error("고정 키 이미지 업로드 실패: {}", e.getMessage());
        }
    }

    /* 기존 텍스트 업로드 테스트 */
    public void testUpload() {
        try {
            String key = "test/upload-test.txt";
            String content = "AWS S3 업로드 테스트 - " + System.currentTimeMillis();

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(awsS3Properties.getBucket())
                .key(key)
                .contentType("text/plain; charset=UTF-8")
                .contentEncoding("UTF-8")
                .build();

            PutObjectResponse response = s3Client.putObject(
                putObjectRequest,
                RequestBody.fromString(content, StandardCharsets.UTF_8)
            );

            log.info("텍스트 업로드 성공 - Key : {}, ETag : {}", key, response.eTag());
        } catch (Exception e) {
            log.error("텍스트 업로드 실패 : {}", e.getMessage(), e);
        }
    }

    /* 기존 텍스트 다운로드 테스트 */
    public void testDownload() {
        try {
            String key = "test/upload-test.txt";

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(awsS3Properties.getBucket())
                .key(key)
                .build();

            ResponseInputStream<GetObjectResponse> response = s3Client.getObject(getObjectRequest);

            String content = new String(response.readAllBytes(), StandardCharsets.UTF_8);
            response.close();

            log.info("텍스트 다운로드 성공 - Key: {}, Content: {}", key, content);

        } catch (Exception e) {
            log.error("텍스트 다운로드 실패: {}", e.getMessage(), e);
        }
    }

    /* 기존 PresignedURL 테스트 */
    public void testPresignedUrl() {
        try {
            String key = "test/upload-test.txt";

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(awsS3Properties.getBucket())
                .key(key)
                .responseContentType("text/plain; charset=UTF-8")
                .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .getObjectRequest(getObjectRequest)
                .build();

            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
            URL presignedUrl = presignedRequest.url();

            log.info("텍스트 PresignedURL 생성 성공 - Key: {}, URL: {}", key, presignedUrl);
        } catch (Exception e) {
            log.error("텍스트 PresignedURL 생성 실패 : {}", e.getMessage(), e);
        }
    }

    /* 이미지 전용 테스트 실행 */
    public void runImageTests() {
        log.info("🖼️ AWS S3 이미지 테스트 시작!");
        log.info("=".repeat(50));

        testImageUpload();
        testImageDownload();
        testImagePresignedUrl();

        log.info("=".repeat(50));
        log.info("AWS S3 이미지 테스트 종료!");
    }

    /* 모든 테스트 실행 */
    public void runAllTests() {
        log.info("AWS S3 전체 테스트 시작!");
        log.info("=".repeat(50));

        log.info("텍스트 파일 테스트:");
        testUpload();
        testDownload();
        testPresignedUrl();

        log.info("");

        log.info("이미지 파일 테스트:");
        testImageUpload();
        testImageDownload();
        testImagePresignedUrl();

        log.info("=".repeat(50));
        log.info("AWS S3 전체 테스트 종료!");
    }
}
