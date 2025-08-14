package com.sprint.mission.discodeit.support;

import org.springframework.stereotype.Component;

import io.github.cdimascio.dotenv.Dotenv;

@Component
public class TestEnvConfig {

    private static final Dotenv dotenv = Dotenv.configure()
        .directory("./") // 프로젝트 루트에서 .env 파일을 읽음
        .ignoreIfMissing()
        .load();

    public final String awsS3AccessKey = dotenv.get("AWS_S3_ACCESS_KEY");
    public final String awsS3SecretKey = dotenv.get("AWS_S3_SECRET_KEY");
    public final String awsS3Bucket = dotenv.get("AWS_S3_BUCKET");
    public final String awsS3Region = dotenv.get("AWS_S3_REGION");
}
