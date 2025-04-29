package com.sprint.mission.discodeit.fixture;

import com.sprint.mission.discodeit.entity.BinaryContent;
import java.util.UUID;

public class BinaryContentFixture {

  private static final String DEFAULT_FILE_NAME = "test.png";
  private static final String DEFAULT_MIME_TYPE = "image/png";
  private static final byte[] DEFAULT_DATA = new byte[]{1, 2, 3};

  public static BinaryContent createValidProfileImage(UUID userId) {
    return BinaryContent.createProfileImage(DEFAULT_DATA, DEFAULT_FILE_NAME, DEFAULT_MIME_TYPE,
        userId);
  }

  public static BinaryContent createValidMessageAttachment(UUID messageId) {
    return BinaryContent.createMessageAttachment(DEFAULT_DATA, DEFAULT_FILE_NAME, DEFAULT_MIME_TYPE,
        messageId);
  }

  public static BinaryContent createCustomProfileImage(byte[] data, String fileName,
      String mimeType, UUID userId) {
    return BinaryContent.createProfileImage(data, fileName, mimeType,
        userId);
  }

  public static BinaryContent createCustomMessageAttachment(byte[] data, String fileName,
      String mimeType, UUID messageId) {
    return BinaryContent.createMessageAttachment(data, fileName, mimeType,
        messageId);
  }
}