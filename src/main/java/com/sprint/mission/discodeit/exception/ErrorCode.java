package com.sprint.mission.discodeit.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

//TODO : 다국어처리
@Getter
@AllArgsConstructor
public enum ErrorCode {

  // 시스템 오류 (5xx)
  DATABASE_ERROR(500, "SYS_001", "데이터베이스 오류가 발생했습니다"),
  EXTERNAL_API_ERROR(500, "SYS_002", "외부 API 호출에 실패했습니다"),
  INTERNAL_SERVER_ERROR(500, "SYS_999", "내부 서버 오류가 발생했습니다"),

  // 일반적인 오류 (4xx)
  INVALID_INPUT(400, "COMMON_001", "입력값이 올바르지 않습니다"),
  RESOURCE_NOT_FOUND(404, "COMMON_002", "요청한 리소스를 찾을 수 없습니다"),
  ACCESS_DENIED(403, "COMMON_003", "접근 권한이 없습니다"),
  INVALID_REQUEST_PARAMS(400, "COMMON_004", "잘못된 파라미터 타입입니다"),

  // User 관련 오류 (4xx)
  USER_NOT_FOUND(404, "USER_001", "사용자를 찾을 수 없습니다"),
  DUPLICATE_EMAIL_NAME(400, "USER_002", "해당하는 이메일/이름을 가진 유저가 이미 존재합니다."),
  INVALID_CREDENTIALS(400, "USER_003", "비밀번호 또는 사용자 정보가 올바르지 않습니다."),

  // UserStatus 관련 오류 (4xx)
  DUPLICATE_USERSTATUS(400, "USERSTATUS_001", "해당하는 유저상태가 이미 존재합니다."),

  // Channel 관련 오류 (4xx)
  PRIVATE_CHANNEL_UPDATE(400, "CHANNEL_001", "private 채널은 수정이 불가능합니다."),

  // ReadStatus 관련 오류 (4xx)
  DUPLICATE_READSTATUS(400, "READSTATUS_001", "해당하는 읽음상태가 이미 존재합니다."),

  // Message 관련 오류 (4xx)
  ACCESS_DENIED_MESSAGE(400, "MESSAGE_001", "유저가 참여하지 않은 채팅방에는 메세지를 보낼수 없습니다.");

  // BinaryContent 관련 오류 (4xx)

  private final int httpStatus;
  private final String code;
  private final String defaultMessage;
}
