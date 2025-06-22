package com.sprint.mission.discodeit.exception.Common;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class InvalidInputException extends CommonException {

  public InvalidInputException() {
    super(ErrorCode.INVALID_INPUT);
  }

  public InvalidInputException(String providedInput) {
    super(ErrorCode.INVALID_INPUT,
        String.format("입력값이 올바르지 않습니다:providedInput=%s", providedInput));
  }

  public InvalidInputException(String providedInput, Throwable cause) {
    super(ErrorCode.INVALID_INPUT,
        String.format("입력값이 올바르지 않습니다:providedInput=%s", providedInput), cause);
  }
}
