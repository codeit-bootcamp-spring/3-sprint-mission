package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.user.DuplicateUserException;
import com.sprint.mission.discodeit.exception.user.InvalidPasswordException;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.readstatus.DuplicateReadStatusException;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.exception.binarycontent.FileUploadFailedException;
import com.sprint.mission.discodeit.exception.binarycontent.FileDeleteFailedException;
import com.sprint.mission.discodeit.exception.userstatus.InvalidUserStatusException;
import com.sprint.mission.discodeit.exception.userstatus.DuplicateUserStatusException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * 전역 예외 처리기
 * 
 * 모든 예외를 일관된 ErrorResponse 형식으로 처리하여
 * 클라이언트에게 구조화된 오류 정보를 제공합니다.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  // === DiscodeitException 기본 처리 ===

  /**
   * 모든 DiscodeitException의 기본 처리기
   * 하위 예외들이 처리되지 않은 경우 이 핸들러가 동작합니다.
   */
  @ExceptionHandler(DiscodeitException.class)
  public ResponseEntity<ErrorResponse> handleDiscodeitException(DiscodeitException e) {
    log.warn("Discodeit 예외 발생: {} - {}", e.getErrorCode(), e.getMessage());

    ErrorResponse errorResponse = ErrorResponse.of(e);
    HttpStatus status = e.getErrorCode().getHttpStatus();

    return ResponseEntity
        .status(status)
        .body(errorResponse);
  }

  // === 사용자 관련 예외들 ===

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException e) {
    log.warn("사용자를 찾을 수 없음: {}", e.getMessage());

    ErrorResponse errorResponse = ErrorResponse.of(e);

    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(errorResponse);
  }

  @ExceptionHandler(DuplicateUserException.class)
  public ResponseEntity<ErrorResponse> handleDuplicateUserException(DuplicateUserException e) {
    log.warn("중복된 사용자: {}", e.getMessage());

    ErrorResponse errorResponse = ErrorResponse.of(e);

    return ResponseEntity
        .status(HttpStatus.CONFLICT)
        .body(errorResponse);
  }

  @ExceptionHandler(InvalidPasswordException.class)
  public ResponseEntity<ErrorResponse> handleInvalidPasswordException(InvalidPasswordException e) {
    log.warn("잘못된 비밀번호: {}", e.getMessage());

    ErrorResponse errorResponse = ErrorResponse.of(e);

    return ResponseEntity
        .status(HttpStatus.UNAUTHORIZED)
        .body(errorResponse);
  }

  // === 사용자 상태 관련 예외들 ===

  @ExceptionHandler(InvalidUserStatusException.class)
  public ResponseEntity<ErrorResponse> handleInvalidUserStatusException(InvalidUserStatusException e) {
    log.warn("잘못된 사용자 상태: {}", e.getMessage());

    ErrorResponse errorResponse = ErrorResponse.of(e);

    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(errorResponse);
  }

  @ExceptionHandler(DuplicateUserStatusException.class)
  public ResponseEntity<ErrorResponse> handleDuplicateUserStatusException(DuplicateUserStatusException e) {
    log.warn("중복된 사용자 상태: {}", e.getMessage());

    ErrorResponse errorResponse = ErrorResponse.of(e);

    return ResponseEntity
        .status(HttpStatus.CONFLICT)
        .body(errorResponse);
  }

  // === 채널 관련 예외들 ===

  @ExceptionHandler(ChannelNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleChannelNotFoundException(ChannelNotFoundException e) {
    log.warn("채널을 찾을 수 없음: {}", e.getMessage());

    ErrorResponse errorResponse = ErrorResponse.of(e);

    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(errorResponse);
  }

  @ExceptionHandler(PrivateChannelUpdateException.class)
  public ResponseEntity<ErrorResponse> handlePrivateChannelUpdateException(PrivateChannelUpdateException e) {
    log.warn("비공개 채널 수정 시도: {}", e.getMessage());

    ErrorResponse errorResponse = ErrorResponse.of(e);

    return ResponseEntity
        .status(HttpStatus.FORBIDDEN)
        .body(errorResponse);
  }

  // === 메시지 관련 예외들 ===

  @ExceptionHandler(MessageNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleMessageNotFoundException(MessageNotFoundException e) {
    log.warn("메시지를 찾을 수 없음: {}", e.getMessage());

    ErrorResponse errorResponse = ErrorResponse.of(e);

    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(errorResponse);
  }

  // === 읽기 상태 관련 예외들 ===

  @ExceptionHandler(ReadStatusNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleReadStatusNotFoundException(ReadStatusNotFoundException e) {
    log.warn("읽기 상태를 찾을 수 없음: {}", e.getMessage());

    ErrorResponse errorResponse = ErrorResponse.of(e);

    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(errorResponse);
  }

  @ExceptionHandler(DuplicateReadStatusException.class)
  public ResponseEntity<ErrorResponse> handleDuplicateReadStatusException(DuplicateReadStatusException e) {
    log.warn("중복된 읽기 상태: {}", e.getMessage());

    ErrorResponse errorResponse = ErrorResponse.of(e);

    return ResponseEntity
        .status(HttpStatus.CONFLICT)
        .body(errorResponse);
  }

  // === 바이너리 콘텐츠 관련 예외들 ===

  @ExceptionHandler(BinaryContentNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleBinaryContentNotFoundException(BinaryContentNotFoundException e) {
    log.warn("바이너리 콘텐츠를 찾을 수 없음: {}", e.getMessage());

    ErrorResponse errorResponse = ErrorResponse.of(e);

    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(errorResponse);
  }

  @ExceptionHandler(FileUploadFailedException.class)
  public ResponseEntity<ErrorResponse> handleFileUploadFailedException(FileUploadFailedException e) {
    log.error("파일 업로드 실패: {}", e.getMessage());

    ErrorResponse errorResponse = ErrorResponse.of(e);

    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(errorResponse);
  }

  @ExceptionHandler(FileDeleteFailedException.class)
  public ResponseEntity<ErrorResponse> handleFileDeleteFailedException(FileDeleteFailedException e) {
    log.error("파일 삭제 실패: {}", e.getMessage());

    ErrorResponse errorResponse = ErrorResponse.of(e);

    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(errorResponse);
  }

  // === 일반적인 예외들 ===

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
    log.warn("잘못된 요청 파라미터: {}", e.getMessage());

    ErrorResponse errorResponse = ErrorResponse.of(
        HttpStatus.BAD_REQUEST.value(),
        e.getClass().getSimpleName(),
        e.getMessage());

    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(errorResponse);
  }

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<ErrorResponse> handleNoSuchElementException(NoSuchElementException e) {
    log.warn("리소스를 찾을 수 없음: {}", e.getMessage());

    ErrorResponse errorResponse = ErrorResponse.of(
        HttpStatus.NOT_FOUND.value(),
        e.getClass().getSimpleName(),
        "요청한 정보를 찾을 수 없습니다.");

    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(errorResponse);
  }

  // === 유효성 검증 예외들 ===

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
    String errorMessage = e.getBindingResult().getFieldErrors().stream()
        .map(FieldError::getDefaultMessage)
        .collect(Collectors.joining(", "));

    log.warn("유효성 검증 실패: {}", errorMessage);

    // 필드별 상세 오류 정보 수집
    var fieldErrors = e.getBindingResult().getFieldErrors().stream()
        .collect(Collectors.toMap(
            FieldError::getField,
            error -> {
              return java.util.Map.of(
                  "rejectedValue", error.getRejectedValue() != null ? error.getRejectedValue().toString() : "null",
                  "message", error.getDefaultMessage() != null ? error.getDefaultMessage() : "유효하지 않은 값입니다.");
            },
            (existing, replacement) -> existing // 중복 키 처리
        ));

    ErrorResponse errorResponse = ErrorResponse.of(
        HttpStatus.BAD_REQUEST.value(),
        e.getClass().getSimpleName(),
        "입력값 검증에 실패했습니다.",
        java.util.Map.of("fieldErrors", fieldErrors));

    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(errorResponse);
  }

  @ExceptionHandler(BindException.class)
  public ResponseEntity<ErrorResponse> handleBindException(BindException e) {
    String errorMessage = e.getBindingResult().getFieldErrors().stream()
        .map(FieldError::getDefaultMessage)
        .collect(Collectors.joining(", "));

    log.warn("바인딩 오류: {}", errorMessage);

    ErrorResponse errorResponse = ErrorResponse.of(
        HttpStatus.BAD_REQUEST.value(),
        e.getClass().getSimpleName(),
        errorMessage);

    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(errorResponse);
  }

  // === 기본 예외 처리 ===

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
    log.error("예상치 못한 오류 발생: {}", e.getMessage(), e);

    ErrorResponse errorResponse = ErrorResponse.of(
        HttpStatus.INTERNAL_SERVER_ERROR.value(),
        e.getClass().getSimpleName(),
        "서버 내부 오류가 발생했습니다.");

    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(errorResponse);
  }
}
