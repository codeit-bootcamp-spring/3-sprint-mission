package com.sprint.mission.discodeit.fixture;

import com.sprint.mission.discodeit.entity.BinaryContent;
import java.util.UUID;

public class BinaryContentFixture {

  private static final String DEFAULT_FILE_NAME = "test.png";
  private static final String DEFAULT_MIME_TYPE = "image/png";
  private static final byte[] DEFAULT_DATA = new byte[]{1, 2, 3};

  /**
   * 기본 프로필 이미지를 생성한다.
   */
  public static BinaryContent createValidProfileImage(UUID userId) {
    return BinaryContent.createProfileImage(DEFAULT_DATA, DEFAULT_FILE_NAME, DEFAULT_MIME_TYPE,
        userId);
  }

  /**
   * 기본 메시지 첨부파일을 생성한다.
   */
  public static BinaryContent createValidMessageAttachment(UUID messageId) {
    return BinaryContent.createMessageAttachment(DEFAULT_DATA, DEFAULT_FILE_NAME, DEFAULT_MIME_TYPE,
        messageId);
  }

  /**
   * 커스텀 프로필 이미지를 생성한다.
   */
  public static BinaryContent createCustomProfileImage(byte[] data, String fileName,
      String mimeType, UUID userId) {
    return BinaryContent.createProfileImage(data, fileName, mimeType, userId);
  }

  /**
   * 커스텀 메시지 첨부파일을 생성한다.
   */
  public static BinaryContent createCustomMessageAttachment(byte[] data, String fileName,
      String mimeType, UUID messageId) {
    return BinaryContent.createMessageAttachment(data, fileName, mimeType, messageId);
  }

  /**
   * 테스트용 기본 데이터 바이트 배열을 반환한다.
   */
  public static byte[] getDefaultData() {
    return DEFAULT_DATA;
  }

  /**
   * 테스트용 기본 파일명을 반환한다.
   */
  public static String getDefaultFileName() {
    return DEFAULT_FILE_NAME;
  }

  /**
   * 테스트용 기본 MIME 타입을 반환한다.
   */
  public static String getDefaultMimeType() {
    return DEFAULT_MIME_TYPE;
  }
}
