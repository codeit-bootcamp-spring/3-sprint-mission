package com.sprint.mission.discodeit.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
  // User
  DUPLICATE_USER("U001", "이미 존재하는 사용자입니다."),
  USER_NOT_FOUND("U002", "존재하지 않는 사용자입니다."),

  // Auth
  USER_PASSWORD_NOT_MATCHED("A001", "비밀번호가 일치하지 않습니다."),

  // UserStatus
  DUPLICATE_USER_STATUS("US001", "이미 존재하는 유저 상태입니다."),
  USER_STATUS_NOT_FOUND("US002", "존재하지 않는 유저 상태입니다."),

  // Channel
  CHANNEL_NOT_FOUND("C002", "존재하지 않는 채널입니다."),
  PRIVATE_CHANNEL_UPDATE("C003", "비공개 채널은 수정할 수 없습니다."),

  // ReadStatus
  DUPLICATE_READ_STATUS("RS001", "이미 존재하는 읽음 상태입니다."),
  READ_STATUS_NOT_FOUND("RS002", "존재하지 않는 읽음 상태입니다."),

  // Message
  MESSAGE_NOT_FOUND("M002", "존재하지 않는 메시지입니다."),

  // BinaryContent
  BINARY_CONTENT_NOT_FOUND("BC002", "존재하지 않는 파일입니다."),

  // Server
  INVALID_REQUEST("S001", "잘못된 요청입니다."),
  INTERNAL_SERVER_ERROR("S002", "서버 내부 오류가 발생했습니다."),

  // Validation
  VALIDATION_ERROR("V001", "유효성 검증에 실패했습니다.");

  private final String code;
  private final String message;

  ErrorCode(String code, String message) {
    this.code = code;
    this.message = message;
  }

}
