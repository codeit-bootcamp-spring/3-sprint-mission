package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class BinaryContentNotFoundException extends BinaryContentException {

  public BinaryContentNotFoundException() {
    super(ErrorCode.BINARY_CONTENT_NOT_FOUND, "BinaryContent를 찾을 수 없습니다.");
  }

  public BinaryContentNotFoundException(String id) {
    super(ErrorCode.BINARY_CONTENT_NOT_FOUND,
        "BinaryContent를 찾을 수 없습니다. [id: " + id + "]");
  }
}
