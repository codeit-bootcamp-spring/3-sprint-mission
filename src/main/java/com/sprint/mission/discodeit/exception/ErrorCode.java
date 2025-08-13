package com.sprint.mission.discodeit.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

  // === 일반적인 예외들 ===
  INVALID_REQUEST("잘못된 요청입니다.", HttpStatus.BAD_REQUEST),
  RESOURCE_NOT_FOUND("요청한 리소스를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  INTERNAL_SERVER_ERROR("서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

  // === 사용자 관련 예외들 ===
  USER_NOT_FOUND("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  DUPLICATE_USER("이미 존재하는 사용자입니다.", HttpStatus.CONFLICT),
  INVALID_PASSWORD("잘못된 비밀번호입니다.", HttpStatus.UNAUTHORIZED),
  DUPLICATE_EMAIL("이미 사용 중인 이메일입니다.", HttpStatus.CONFLICT),
  DUPLICATE_USERNAME("이미 사용 중인 사용자명입니다.", HttpStatus.CONFLICT),

  // === 사용자 상태 관련 예외들 ===
  USER_STATUS_NOT_FOUND("사용자 상태를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  INVALID_USER_STATUS("잘못된 사용자 상태입니다.", HttpStatus.BAD_REQUEST),
  DUPLICATE_USER_STATUS("이미 존재하는 사용자 상태입니다.", HttpStatus.CONFLICT),

  // === 채널 관련 예외들 ===
  CHANNEL_NOT_FOUND("채널을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  PRIVATE_CHANNEL_UPDATE("비공개 채널은 수정할 수 없습니다.", HttpStatus.FORBIDDEN),
  DUPLICATE_PARTICIPANTS("중복된 참가자가 있습니다.", HttpStatus.BAD_REQUEST),
  INSUFFICIENT_PARTICIPANTS("비공개 채널은 최소 2명의 참가자가 필요합니다.", HttpStatus.BAD_REQUEST),

  // === 메시지 관련 예외들 ===
  MESSAGE_NOT_FOUND("메시지를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  MESSAGE_CONTENT_REQUIRED("메시지 내용은 필수입니다.", HttpStatus.BAD_REQUEST),
  MESSAGE_ATTACHMENT_ERROR("메시지 첨부파일 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

  // === 읽기 상태 관련 예외들 ===
  READ_STATUS_NOT_FOUND("읽기 상태를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  DUPLICATE_READ_STATUS("이미 존재하는 읽기 상태입니다.", HttpStatus.CONFLICT),

  // === 바이너리 콘텐츠 관련 예외들 ===
  BINARY_CONTENT_NOT_FOUND("파일을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  FILE_UPLOAD_FAILED("파일 업로드에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  FILE_DOWNLOAD_FAILED("파일 다운로드에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  FILE_DELETE_FAILED("파일 삭제에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  FILE_STORAGE_INIT_FAILED("파일 저장소 초기화에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  FILE_READ_FAILED("파일 읽기에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

  // === 유효성 검증 관련 예외들 ===
  VALIDATION_FAILED("유효성 검증에 실패했습니다.", HttpStatus.BAD_REQUEST),
  BINDING_ERROR("요청 데이터 바인딩 중 오류가 발생했습니다.", HttpStatus.BAD_REQUEST),

  // === 데이터베이스 관련 예외들 ===
  DATABASE_ERROR("데이터베이스 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  CONSTRAINT_VIOLATION("데이터 제약 조건을 위반했습니다.", HttpStatus.BAD_REQUEST);

  private final String message;
  private final HttpStatus httpStatus;

  /**
   * HTTP 상태 코드를 반환합니다.
   * 
   * @return HTTP 상태 코드 값
   */
  public int getStatusCode() {
    return httpStatus.value();
  }
}
