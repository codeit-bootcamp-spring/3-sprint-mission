package com.sprint.mission.discodeit.storage.s3;


import com.sprint.mission.discodeit.config.AwsS3Properties;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.logging.Logger;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

public class AWSS3Test {
    private static final Logger logger = Logger.getLogger(AWSS3Test.class.getName());

    private final String bucket = AwsS3Properties.get("AWS_S3_BUCKET");

    private final S3Client s3Client = S3Client.builder()
            .region(Region.of(AwsS3Properties.get("AWS_S3_REGION")))
            .credentialsProvider(
                    StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(
                                    AwsS3Properties.get("AWS_S3_ACCESS_KEY"),
                                    AwsS3Properties.get("AWS_S3_SECRET_KEY")
                            )
                    )
            )
            .build();

    // 업로드 테스트
    public void uploadTest() {
        String key = "uploads/favicon2.ico"; // S3 내에서 저장될 경로
        String filePath = "src/main/resources/static/favicon.ico"; // 로컬 파일 경로

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .build(),
                RequestBody.fromFile(Paths.get(filePath))
        );

        logger.info("S3에 내 로컬에 있는 파일 업로드 완료");
    }

    //다운로드 테스트
    public void downloadTest(){
        String key = "uploads/favicon.ico";
        String downloadPath = "src/main/resources/static/s3test/favicon-download2.ico"; // 로컬 파일 경로

        s3Client.getObject(
                GetObjectRequest.builder().bucket(bucket).key(key).build(),
                Paths.get(downloadPath)
        );

        logger.info("S3에서 내 로컬에 파일 다운로드 완료");
    }

    //버킷 내 파일 목록 조회
    public void getBucketFile(){
        ListObjectsV2Response listObjectsResponse = s3Client.listObjectsV2(
                ListObjectsV2Request.builder().bucket(bucket).build()
        );

        logger.info("버킷 내 파일 목록 조회");

        listObjectsResponse.contents().forEach(object -> {
            logger.info("File: " + object.key());
        });
    }

    // Presigned URL 생성
    public void presignedUrlTest(){
        String key = "uploads/favicon.ico";

        String presignedUrl;
        try (S3Presigner presigner = S3Presigner.builder()
                .region(Region.of(AwsS3Properties.get("AWS_S3_REGION")))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        AwsS3Properties.get("AWS_S3_ACCESS_KEY"),
                                        AwsS3Properties.get("AWS_S3_SECRET_KEY")
                                )
                        )
                )
                .build()) {

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(5)) // 5분 동안 유효
                    .getObjectRequest(GetObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .build())
                    .build();

            presignedUrl = presigner.presignGetObject(presignRequest).url().toString();
        }
        logger.info("Presigned URL: " + presignedUrl);
    }

    public static void main(String[] args) {
        AWSS3Test test = new AWSS3Test();

        // 원하는 테스트만 주석 해제해서 실행
//        test.uploadTest();
        test.downloadTest();
//        test.getBucketFile();
//        test.presignedUrlTest();
    }
}
