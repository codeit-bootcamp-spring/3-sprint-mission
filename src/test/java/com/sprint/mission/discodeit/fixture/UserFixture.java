package com.sprint.mission.discodeit.fixture;

import com.sprint.mission.discodeit.entity.User;
import java.util.UUID;

/**
 * User 테스트를 위한 설비
 */
public class UserFixture {

  private static final String EMAIL_DOMAIN = "test.com";

  // UUID를 사용한 랜덤 이메일 생성
  private static String generateRandomEmail() {
    return UUID.randomUUID().toString() + "@" + EMAIL_DOMAIN;
  }

  public static final String DEFAULT_NAME = "테스트 유저";
  public static final String DEFAULT_PASSWORD = "1234";

  public static User createDefaultUser() {
    return User.create(generateRandomEmail(), DEFAULT_NAME, DEFAULT_PASSWORD);
  }

  public static User createCustomUser(String email, String name, String password) {
    return User.create(email, name, password);
  }
}