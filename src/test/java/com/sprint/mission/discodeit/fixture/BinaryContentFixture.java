package com.sprint.mission.discodeit.fixture;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;

public class BinaryContentFixture {

  private static final String DEFAULT_FILE_NAME = "test.png";
  private static final String DEFAULT_MIME_TYPE = "image/png";
  private static final byte[] DEFAULT_DATA = new byte[]{1, 2, 3};

  /**
   * 기본 BinaryContent 객체를 생성한다.
   */
  public static BinaryContent createValid() {
    return BinaryContent.create(DEFAULT_FILE_NAME, (long) DEFAULT_DATA.length, DEFAULT_MIME_TYPE,
        DEFAULT_DATA);
  }

  /**
   * 커스텀 BinaryContent 객체를 생성한다.
   */
  public static BinaryContent createCustom(BinaryContentCreateRequest dto) {
    return BinaryContent.create(dto.fileName(), (long) dto.bytes().length, dto.contentType(),
        dto.bytes());
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
