package com.sprint.mission.discodeit.testconfig;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * 테스트 환경에서 환경 변수를 로드하는 설정 클래스입니다.
 * <p>
 * 환경 변수는 기본적으로 application.yaml에서 임포트하고 있지만 동적 값이 필요한 경우 사용할 수 있는 테스트용 환경 변수 설정입니다.
 * <p>
 * - 기본적으로 프로젝트 루트의 .env 파일만 읽습니다.<br> - .env* 패턴(예: .env.local, .env.test 등)은 자동으로 모두 읽지 않습니다.<br>
 * - systemProperties() 옵션을 통해 시스템 환경 변수(process.env)도 함께 읽습니다.<br> - 동일한 키가 .env 파일과 시스템 환경 변수에 모두
 * 존재할 경우, 시스템 환경 변수 값이 우선 적용됩니다.<br>
 * </p>
 */
@Profile("test | security-test")
@Component
public class TestEnvConfig {

  private static final Dotenv dotenv = Dotenv.configure()
      .directory("./")
      .ignoreIfMissing()
      .systemProperties()
      .load();

  private String getEnv(String key) {
    String value = System.getenv(key);
    if (value != null && !value.isEmpty()) {
      return value;
    }
    return dotenv.get(key, "");
  }

  public final String awsS3AccessKey = getEnv("AWS_S3_ACCESS_KEY");
  public final String awsS3SecretKey = getEnv("AWS_S3_SECRET_KEY");
  public final String awsS3Bucket = getEnv("AWS_S3_BUCKET");
  public final String awsS3Region = getEnv("AWS_S3_REGION");
  public final String discodeitAdminUsername = getEnv("DISCODEIT_ADMIN_USERNAME");
  public final String discodeitAdminEmail = getEnv("DISCODEIT_ADMIN_EMAIL");
  public final String discodeitAdminPassword = getEnv("DISCODEIT_ADMIN_PASSWORD");
}
