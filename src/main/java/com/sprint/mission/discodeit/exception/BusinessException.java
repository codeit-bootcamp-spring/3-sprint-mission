package com.sprint.mission.discodeit.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

  private final ErrorCode errorCode;

  public BusinessException(ErrorCode errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
  }

  @Override
  public String toString() {
    return String.format("%s: [%s] %s",
        getClass().getName(),
        errorCode.getMessage(),
        getMessage());
  }
}