package com.sprint.mission.discodeit.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
  UNAUTHORIZED("인증이 필요합니다.", HttpStatus.UNAUTHORIZED),
  INVALID_INPUT("입력값이 유효하지 않습니다.", HttpStatus.BAD_REQUEST),

  USER_NOT_FOUND("해당 사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  USER_ALREADY_EXISTS("이미 존재하는 사용자입니다.", HttpStatus.CONFLICT),
  USER_STATUS_NOT_FOUND("UserStatus를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  USER_STATUS_ALREADY_EXISTS("이미 존재하는 UserStatus입니다.", HttpStatus.CONFLICT),

  CHANNEL_NOT_FOUND("해당 채널을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  CHANNEL_ALREADY_EXISTS("이미 존재하는 채널입니다.", HttpStatus.CONFLICT),
  CHANNEL_CLOSED("이미 종료된 채널입니다.", HttpStatus.BAD_REQUEST),
  PARTICIPANT_NOT_FOUND("채널에서 사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  PARTICIPANT_ALREADY_EXISTS("이미 채널에 참여 중인 사용자입니다.", HttpStatus.CONFLICT),
  CANNOT_UPDATE_PRIVATE_CHANNEL("비공개 채널은 수정할 수 없습니다.", HttpStatus.FORBIDDEN),

  MESSAGE_NOT_FOUND("해당 메시지를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  MESSAGE_PROCESSING_ERROR("메시지 저장에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  MESSAGE_PERMISSION_DENIED("해당 메시지에 대한 권한이 없습니다.", HttpStatus.FORBIDDEN),

  BINARY_CONTENT_NOT_FOUND("파일을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  BINARY_CONTENT_PROCESSING_ERROR("BinaryContent 처리 중 오류가 발생했습니다.",
      HttpStatus.INTERNAL_SERVER_ERROR),
  INVALID_BINARY_FORMAT("잘못된 파일 형식입니다.", HttpStatus.BAD_REQUEST),

  READ_STATUS_NOT_FOUND("읽기 상태 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  READ_STATUS_ALREADY_EXISTS("이미 존재하는 읽기 상태 정보입니다.", HttpStatus.CONFLICT),
  READ_STATUS_INVALID_USER_OR_CHANNEL("유효하지 않은 사용자 또는 채널입니다.", HttpStatus.BAD_REQUEST);

  private final String message;
  private final int status;

  ErrorCode(String message, HttpStatus status) {
    this.message = message;
    this.status = status.value();
  }
}
