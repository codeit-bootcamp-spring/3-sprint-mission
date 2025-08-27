package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.event.BinaryContentNotUploadedEvent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/**
 * AWS S3를 활용하여 바이너리 콘텐츠를 저장/조회/다운로드하는 저장소 구현체입니다.
 * 
 * <p>Spring Retry를 활용한 안정적인 업로드 및 다운로드를 제공하며,
 * S3 Presigned URL을 통한 안전한 파일 다운로드를 지원합니다.</p>
 * 
 * <p>주요 특징:</p>
 * <ul>
 *   <li>AWS S3 기반 파일 저장 및 관리</li>
 *   <li>Spring Retry를 통한 자동 재시도</li>
 *   <li>Presigned URL을 통한 안전한 다운로드</li>
 *   <li>이벤트 기반 오류 처리</li>
 *   <li>조건부 빈 등록 (S3 스토리지 타입일 때만)</li>
 * </ul>
 * 
 * @author HuInDoL
 * @since 1.0.0
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
public class S3BinaryContentStorage implements BinaryContentStorage {

    private static final String STORAGE_NAME = "[S3BinaryContentStorage] ";

    private final ApplicationEventPublisher eventPublisher;

    // AWS S3 인증 정보 및 설정값
    private final String accessKey;
    private final String secretKey;
    private final String region;
    private final String bucket;
    @Value("${discodeit.storage.s3.presigned-url-expiration:600}")
    public int presignedUrlExpiration;

    /**
     * 환경변수를 통해 설정값을 주입받아 Presigner를 초기화한다.
     * S3Client는 필요 시마다 생성하여 사용한다.
     */
    public S3BinaryContentStorage(
            @Value("${discodeit.storage.s3.access-key}") String accessKey,
            @Value("${discodeit.storage.s3.secret-key}") String secretKey,
            @Value("${discodeit.storage.s3.region}") String region,
            @Value("${discodeit.storage.s3.bucket}") String bucket,
            ApplicationEventPublisher eventPublisher
    ) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.region = region;
        this.bucket = bucket;
        this.eventPublisher = eventPublisher;
    }

    /**
     * 주어진 ID와 콘텐츠를 S3에 업로드한다.
     * Spring Retry를 활용하여 일시적 오류에 대해 자동 재시도를 수행한다.
     * 
     * @param binaryContentId 업로드할 객체의 UUID
     * @param bytes 저장할 파일의 바이너리 내용
     * @return 저장된 객체의 UUID
     * @throws RuntimeException 업로드가 최종적으로 실패한 경우
     */
    @Override
    @Retryable(
            value = {
                    S3Exception.class,              // S3 서비스 오류
                    SdkException.class,             // AWS SDK 오류
                    SocketTimeoutException.class    // 네트워크 타임아웃
            },
            maxAttempts = 3,                        // 최대 3번 재시도
            backoff = @Backoff(
                    delay = 1000,                   // 첫 번째 재시도까지 1초 대기
                    multiplier = 2,                 // 다음 재시도마다 2배씩 증가 (1초 -> 2초 -> 4초)
                    maxDelay = 10000                // 최대 10초까지 대기
            ),
            exclude = {
                    IllegalArgumentException.class,
                    BinaryContentNotFoundException.class
            }
    )
    public UUID put(UUID binaryContentId, byte[] bytes) {
        try {
            S3Client s3Client = getS3Client();
            String key = generateS3Key(binaryContentId);

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentLength((long) bytes.length)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(bytes));

            log.info(STORAGE_NAME + "파일 업로드 성공: id={}, key={}, size={} bytes",
                    binaryContentId, key, bytes.length);

            return binaryContentId;
            
        } catch (S3Exception e) {
            // S3 특정 오류에 대한 세밀한 처리
            handleS3Exception(e, binaryContentId);
            throw e; // 재시도를 위해 원본 예외를 다시 던짐
            
        } catch (SdkException e) {
            // AWS SDK 오류 처리
            log.warn(STORAGE_NAME + "AWS SDK 오류 발생 - id={}, error={}",
                    binaryContentId, e.getMessage());
            throw e; // 재시도를 위해 원본 예외를 다시 던짐
            
        } catch (Exception e) {
            // 예상치 못한 오류는 재시도하지 않음
            log.error(STORAGE_NAME + "예상치 못한 오류 발생 - id={}, error={}",
                    binaryContentId, e.getMessage(), e);
            throw new RuntimeException("S3 파일 업로드 실패", e);
        }
    }

    /**
     * S3Exception을 분석하여 재시도 여부를 결정하고 적절한 로깅을 수행한다.
     * 
     * @param e S3Exception
     * @param binaryContentId 바이너리 콘텐츠 ID
     */
    private void handleS3Exception(S3Exception e, UUID binaryContentId) {
        int statusCode = e.statusCode();
        String errorCode = e.awsErrorDetails().errorCode();
        
        if (statusCode >= 500) {
            // 5xx 서버 오류: 재시도 대상
            log.warn(STORAGE_NAME + "S3 서버 오류 발생 - id={}, status={}, error={}",
                    binaryContentId, statusCode, errorCode);
        } else if (statusCode == 429) {
            // Rate Limit: 재시도 대상
            log.warn(STORAGE_NAME + "S3 Rate Limit 발생 - id={}, status={}, error={}",
                    binaryContentId, statusCode, errorCode);
        } else if (statusCode >= 400 && statusCode < 500) {
            // 4xx 클라이언트 오류: 재시도 제외
            log.error(STORAGE_NAME + "S3 클라이언트 오류 발생 - id={}, status={}, error={}",
                    binaryContentId, statusCode, errorCode);
            throw new IllegalArgumentException("S3 클라이언트 오류: " + errorCode, e);
        } else {
            // 기타 오류: 재시도 대상
            log.warn(STORAGE_NAME + "S3 오류 발생 - id={}, status={}, error={}",
                    binaryContentId, statusCode, errorCode);
        }
    }

    /**
     * 주어진 ID로 S3에서 객체를 조회하여 InputStream으로 반환한다.
     * @param binaryContentId 다운로드할 객체의 UUID
     * @return InputStream 형태의 객체
     * @throws BinaryContentNotFoundException 객체가 존재하지 않거나 S3 호출에 실패한 경우
     */
    @Override
    public InputStream get(UUID binaryContentId) {
        try {
            S3Client s3Client = getS3Client();
            String key = generateS3Key(binaryContentId);

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();
            
            log.info(STORAGE_NAME + "객체 조회 시작");
            ResponseInputStream<GetObjectResponse> response = s3Client.getObject(getObjectRequest);

            log.info(STORAGE_NAME + "객체 조회 성공 - id={}", binaryContentId);
            return response;
            
        } catch (Exception e) {
            log.error(STORAGE_NAME + "객체 조회 실패 - id={}", binaryContentId, e);
            throw new BinaryContentNotFoundException("S3 객체 다운로드 실패: " + e);
        }
    }

    /**
     * Presigned URL을 생성하고, 이를 통해 다운로드 리다이렉트를 응답으로 반환한다.
     * @param metaData 다운로드 대상 정보
     * @return 302 리다이렉트 응답
     */
    @Override
    public ResponseEntity<Void> download(BinaryContentDto metaData) {
        try {
            String key = generateS3Key(metaData.id());
            String presignedUrl = generatePresignedUrl(key, metaData.contentType());

            log.info(STORAGE_NAME + "Presigned URL 생성 성공 - id={}, filename={}",
                    metaData.id(), metaData.fileName());

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.LOCATION, presignedUrl);

            return new ResponseEntity<>(headers, HttpStatus.FOUND);
            
        } catch (Exception e) {
            log.error(STORAGE_NAME + "Presigned URL 생성 실패 - id={}", metaData.id(), e);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 내부에서 사용할 S3Client를 생성한다.
     * @return 구성된 S3Client 인스턴스
     */
    private S3Client getS3Client() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

    /**
     * S3용 presigned URL을 생성한다.
     * @param key 객체 key
     * @param contentType 콘텐츠 타입
     * @return presigned URL 문자열
     */
    private String generatePresignedUrl(String key, String contentType) {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        try (S3Presigner s3Presigner = S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build())
        {
            String filename = key.contains("/") ? key.substring(key.lastIndexOf('/') + 1) : key;
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .responseContentType(contentType)
                    .responseContentDisposition("attachment: filename=\"" + filename + "\"")
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofSeconds(presignedUrlExpiration))
                    .getObjectRequest(getObjectRequest)
                    .build();

            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
            return presignedRequest.url().toString();
        }
    }

    private String generateS3Key(UUID binaryContentId) {
        String uuidStr = binaryContentId.toString();
        return String.format("binary-content/%s/%s/%s",
                uuidStr.substring(0, 2),
                uuidStr.substring(2, 4),
                uuidStr);
    }

    /**
     * 모든 재시도가 실패한 후 실행되는 복구 로직.
     * 실패 원인을 분석하고 적절한 처리를 수행한다.
     * 
     * @param e 최종적으로 발생한 예외
     * @param binaryContentId 바이너리 콘텐츠 ID
     * @param bytes 업로드하려던 바이트 데이터
     * @return 복구 결과 (실제로는 void이지만 @Recover 메서드의 시그니처를 맞추기 위함)
     */
    @Recover
    public UUID recoverPut(Exception e, UUID binaryContentId, byte[] bytes) {
        log.error(STORAGE_NAME + "모든 재시도 시도 실패 - id={}, error={}",
                binaryContentId, e.getMessage());
        String requestId = MDC.get("traceId");
        if (requestId == null) {
            requestId = "unknown";
        }

        BinaryContentNotUploadedEvent event = new BinaryContentNotUploadedEvent(requestId, binaryContentId, e.getMessage(), Instant.now());
        eventPublisher.publishEvent(event);

        // 실패 원인 분석 및 로깅
        if (e instanceof S3Exception) {
            S3Exception s3Exception = (S3Exception) e;
            log.error(STORAGE_NAME + "최종 S3 오류 상세 - status={}, error={}, requestId={}",
                    s3Exception.statusCode(), 
                    s3Exception.awsErrorDetails().errorCode(),
                    s3Exception.requestId());
        }
        
        // 최종적으로 RuntimeException을 던져서 호출자에게 실패를 알림
        throw new RuntimeException("S3 파일 업로드 최종 실패: " + e.getMessage(), e);
    }
}