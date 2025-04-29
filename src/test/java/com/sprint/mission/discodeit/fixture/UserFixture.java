package com.sprint.mission.discodeit.fixture;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.entity.User;
import java.util.Random;
import java.util.UUID;

/**
 * User 테스트를 위한 설비
 */
public class UserFixture {

  private static final String EMAIL_DOMAIN = "test.com";
  private static final String CHAR_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
  public static final int MIN_NAME_LENGTH = 5;
  public static final int MAX_NAME_LENGTH = 15;
  public static final int MIN_PASSWORD_LENGTH = 8;
  public static final int MAX_PASSWORD_LENGTH = 20;

  private static String generateRandomEmail() {
    return UUID.randomUUID().toString() + "@" + EMAIL_DOMAIN;
  }

  private static String generateRandomString(int length) {
    Random random = new Random();
    StringBuilder builder = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      builder.append(CHAR_POOL.charAt(random.nextInt(CHAR_POOL.length())));
    }
    return builder.toString();
  }

  public static User createValidUser() {
    Random random = new Random();
    int nameLength = MIN_NAME_LENGTH + random.nextInt(MAX_NAME_LENGTH - MIN_NAME_LENGTH + 1);
    int passwordLength =
        MIN_PASSWORD_LENGTH + random.nextInt(MAX_PASSWORD_LENGTH - MIN_PASSWORD_LENGTH + 1);

    String randomName = generateRandomString(nameLength);
    String randomPassword = generateRandomString(passwordLength);

    return User.create(generateRandomEmail(), randomName, randomPassword);
  }

  public static User createCustomUser(String email, String name, String password) {
    return User.create(email, name, password);
  }

  public static User createCustomUser(UserCreateRequest request) {
    UUID profileImageId = null;
    if (request.profileImage() != null) {
      profileImageId = request.profileImage().getId();
    }
    return User.create(request.email(), request.name(), request.password(),
        profileImageId);
  }
}