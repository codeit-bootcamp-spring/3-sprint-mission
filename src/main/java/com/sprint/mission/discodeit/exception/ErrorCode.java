package com.sprint.mission.discodeit.exception;

public enum ErrorCode {
  NOT_FOUND("error.not_found", "E404"),
  INVALID_INPUT("error.invalid_input", "E400"),
  UNAUTHORIZED("error.unauthorized", "E401"),
  FORBIDDEN("error.forbidden", "E403"),
  ALREADY_EXISTS("error.already_exists", "E409"),
  PROCESSING_ERROR("error.processing_error", "E500"),

  FILE_READ_ERROR("error.file.read_error", "F001"),
  FILE_WRITE_ERROR("error.file.write_error", "F002"),
  FILE_NOT_FOUND("error.file.not_found", "F003"),
  INVALID_POSITION("error.file.invalid_position", "F004"),
  SERIALIZATION_ERROR("error.file.serialization_error", "F005"),
  DELETE_ERROR("error.file.delete_error", "F006"),
  OPTIMIZATION_ERROR("error.file.optimization_error", "F007");

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
