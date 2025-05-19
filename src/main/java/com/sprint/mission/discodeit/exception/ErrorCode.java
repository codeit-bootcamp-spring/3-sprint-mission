package com.sprint.mission.discodeit.exception;

public enum ErrorCode {
  NOT_FOUND("error.not_found", "E404"),
  INVALID_INPUT("error.invalid_input", "E400"),
  UNAUTHORIZED("error.unauthorized", "E401"),
  FORBIDDEN("error.forbidden", "E403"),
  ALREADY_EXISTS("error.already_exists", "E409"),
  PROCESSING_ERROR("error.processing_error", "E500");

  private final String messageKey;
  private final String code;

  ErrorCode(String messageKey, String code) {
    this.messageKey = messageKey;
    this.code = code;
  }

  public String getMessageKey() {
    return messageKey;
  }

  public String getCode() {
    return code;
  }
}
