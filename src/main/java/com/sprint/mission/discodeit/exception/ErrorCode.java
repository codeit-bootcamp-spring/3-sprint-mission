package com.sprint.mission.discodeit.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
  NOT_FOUND("NOT_FOUND", "리소스를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  INVALID_INPUT("INVALID_INPUT", "입력값이 유효하지 않습니다.", HttpStatus.BAD_REQUEST),
  UNAUTHORIZED("UNAUTHORIZED", "인증이 필요합니다.", HttpStatus.UNAUTHORIZED),
  FORBIDDEN("FORBIDDEN", "권한이 없습니다.", HttpStatus.FORBIDDEN),
  ALREADY_EXISTS("ALREADY_EXISTS", "이미 존재합니다.", HttpStatus.CONFLICT),
  PROCESSING_ERROR("PROCESSING_ERROR", "처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

  private final String messageKey;
  private final String message;
  private final int status;

  ErrorCode(String messageKey, String message, HttpStatus status) {
    this.messageKey = messageKey;
    this.message = message;
    this.status = status.value();
  }
}
