package com.sprint.mission.discodeit.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
  // User
  USER_NOT_FOUND("U001", "사용자를 찾을 수 없습니다."),
  USER_DUPLICATE_EMAIL("U002", "이미 존재하는 이메일입니다."),

  // Message
  MESSAGE_NOT_FOUND("M001", "메시지를 찾을 수 없습니다."),
  MESSAGE_TOO_LONG("M002", "메시지가 너무 깁니다."),

  // Common
  INTERNAL_SERVER_ERROR("C001", "알 수 없는 서버 오류가 발생했습니다.");

  private final String code;
  private final String message;
}
