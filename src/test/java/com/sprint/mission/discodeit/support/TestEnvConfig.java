package com.sprint.mission.discodeit.support;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Component;

/**
 * 테스트 환경에서 환경 변수를 로드하는 설정 클래스입니다.
 * <p>
 * - 기본적으로 프로젝트 루트의 .env 파일만 읽습니다.<br> - .env* 패턴(예: .env.local, .env.test 등)은 자동으로 모두 읽지 않습니다.<br>
 * - systemProperties() 옵션을 통해 시스템 환경 변수(process.env)도 함께 읽습니다.<br> - 동일한 키가 .env 파일과 시스템 환경 변수에 모두
 * 존재할 경우, 시스템 환경 변수 값이 우선 적용됩니다.<br>
 * </p>
 */
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
