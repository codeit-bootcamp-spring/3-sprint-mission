package com.sprint.mission.discodeit.fixture;

import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import java.util.Random;
import java.util.UUID;

/**
 * User 테스트를 위한 Fixture
 */
public class UserFixture {

  private static final String EMAIL_DOMAIN = "test.com";
  private static final String CHAR_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
  public static final int MIN_NAME_LENGTH = 5;
  public static final int MAX_NAME_LENGTH = 15;
  public static final int MIN_PASSWORD_LENGTH = 8;
  public static final int MAX_PASSWORD_LENGTH = 20;

  /**
   * 랜덤 이메일을 생성한다.
   */
  private static String generateRandomEmail() {
    return UUID.randomUUID() + "@" + EMAIL_DOMAIN;
  }

  /**
   * 랜덤 문자열을 생성한다.
   */
  private static String generateRandomString(int length) {
    Random random = new Random();
    StringBuilder builder = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      builder.append(CHAR_POOL.charAt(random.nextInt(CHAR_POOL.length())));
    }
    return builder.toString();
  }

  /**
   * 기본 유효한 User를 생성한다.
   */
  public static User createValidUser() {
    Random random = new Random();
    int nameLength = MIN_NAME_LENGTH + random.nextInt(MAX_NAME_LENGTH - MIN_NAME_LENGTH + 1);
    int passwordLength =
        MIN_PASSWORD_LENGTH + random.nextInt(MAX_PASSWORD_LENGTH - MIN_PASSWORD_LENGTH + 1);

    String randomName = generateRandomString(nameLength);
    String randomPassword = generateRandomString(passwordLength);

    return User.create(generateRandomEmail(), randomName, randomPassword);
  }

  /**
   * 프로필 이미지를 포함하는 User를 생성한다.
   */
  public static User createValidUserWithProfileImage() {
    User user = createValidUser();
    BinaryContent profileImage = BinaryContentFixture.createValid();
    user.updateProfileId(profileImage.getId());
    return user;
  }

  /**
   * 커스텀 User를 생성한다.
   */
  public static User createCustomUser(String email, String name, String password) {
    return User.create(email, name, password);
  }

  /**
   * UserCreateRequest를 기반으로 User를 생성한다.
   */
  public static User createCustomUser(UserCreateRequest dto, BinaryContent profileImage) {
    UUID profileImageId = null;

    if (profileImage != null) {
      String fileName = profileImage.getFileName();
      String contentType = profileImage.getContentType();
      byte[] bytes = profileImage.getBytes();

      BinaryContent savedProfileImage = BinaryContent.create(fileName, (long) fileName.length(),
          contentType, bytes);

      profileImageId = savedProfileImage.getId();
    }

    return User.create(dto.email(), dto.username(), dto.password(), profileImageId);
  }
}
