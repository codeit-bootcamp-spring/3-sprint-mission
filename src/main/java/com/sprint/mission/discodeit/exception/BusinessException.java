package com.sprint.mission.discodeit.exception;

public class BusinessException extends RuntimeException {

  private final String errorCode;

  public BusinessException(String message, String errorCode) {
    super(message);
    this.errorCode = errorCode;
  }

  public String getErrorCode() {
    return errorCode;
  }
}