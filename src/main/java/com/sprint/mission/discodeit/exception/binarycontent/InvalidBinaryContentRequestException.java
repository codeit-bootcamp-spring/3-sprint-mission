package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class InvalidBinaryContentRequestException extends BinaryContentException {

  public InvalidBinaryContentRequestException() {
    super(ErrorCode.INVALID_BINARY_FORMAT, "요청 형식이 잘못된 BinaryContent입니다.");
  }
}
