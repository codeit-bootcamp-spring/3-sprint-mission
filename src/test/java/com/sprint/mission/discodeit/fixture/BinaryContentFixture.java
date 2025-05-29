package com.sprint.mission.discodeit.fixture;

import com.sprint.mission.discodeit.entity.BinaryContent;
import java.util.UUID;

public class BinaryContentFixture {

  private static final String DEFAULT_FILE_NAME = "test.png";
  private static final String DEFAULT_MIME_TYPE = "image/png";
  private static final byte[] DEFAULT_DATA = new byte[]{1, 2, 3};

  public static BinaryContent createValid() {
    return BinaryContent.create(
        DEFAULT_FILE_NAME,
        (long) DEFAULT_DATA.length,
        DEFAULT_MIME_TYPE,
        DEFAULT_DATA);
  }

  public static BinaryContent createValidWithId() {
    BinaryContent binaryContent = createValid();
    binaryContent.assignIdForTest(UUID.randomUUID());
    return binaryContent;
  }

  public static BinaryContent createCustom(
      String fileName,
      String contentType,
      byte[] bytes
  ) {
    return BinaryContent.create(
        fileName,
        (long) bytes.length,
        contentType,
        bytes);
  }

  public static byte[] getDefaultData() {
    return DEFAULT_DATA;
  }

  public static String getDefaultFileName() {
    return DEFAULT_FILE_NAME;
  }

  public static String getDefaultMimeType() {
    return DEFAULT_MIME_TYPE;
  }
}
