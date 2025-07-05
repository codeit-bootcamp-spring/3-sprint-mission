package com.sprint.mission.discodeit.exception;

public class InvalidInputException extends DiscodeitException {
  public InvalidInputException(String reason) {
    super(ErrorCode.INVALID_INPUT, reason);
  }
}
