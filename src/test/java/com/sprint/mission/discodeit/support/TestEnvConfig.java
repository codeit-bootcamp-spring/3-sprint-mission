package com.sprint.mission.discodeit.support;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Component;

@Component
public class TestEnvConfig {

  private static final Dotenv dotenv = Dotenv.configure()
      .directory("./")
      .ignoreIfMissing()
      .systemProperties()
      .load();

  public final String awsS3AccessKey = dotenv.get("AWS_S3_ACCESS_KEY");
  public final String awsS3SecretKey = dotenv.get("AWS_S3_SECRET_KEY");
  public final String awsS3Bucket = dotenv.get("AWS_S3_BUCKET");
  public final String awsS3Region = dotenv.get("AWS_S3_REGION");
}
