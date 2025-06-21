package com.sprint.mission.discodeit.exception;

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

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  // === 일반적인 예외들 ===

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
    log.warn("잘못된 요청 파라미터: {}", e.getMessage());
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(e.getMessage());
  }

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<String> handleNoSuchElementException(NoSuchElementException e) {
    log.warn("리소스를 찾을 수 없음: {}", e.getMessage());
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body("요청한 정보를 찾을 수 없습니다.");
  }

  // === 유효성 검증 예외들 ===

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException e) {
    String errorMessage = e.getBindingResult().getFieldErrors().stream()
        .map(FieldError::getDefaultMessage)
        .collect(Collectors.joining(", "));

    log.warn("유효성 검증 실패: {}", errorMessage);
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(errorMessage);
  }

  @ExceptionHandler(BindException.class)
  public ResponseEntity<String> handleBindException(BindException e) {
    String errorMessage = e.getBindingResult().getFieldErrors().stream()
        .map(FieldError::getDefaultMessage)
        .collect(Collectors.joining(", "));

    log.warn("바인딩 오류: {}", errorMessage);
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(errorMessage);
  }

  // === 사용자 관련 예외들 ===

  @ExceptionHandler(CustomException.UserNotFoundException.class)
  public ResponseEntity<String> handleUserNotFoundException(CustomException.UserNotFoundException e) {
    log.warn("사용자를 찾을 수 없음: {}", e.getMessage());
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body("사용자 정보를 찾을 수 없습니다.");
  }

  @ExceptionHandler(CustomException.DuplicateUserException.class)
  public ResponseEntity<String> handleDuplicateUserException(CustomException.DuplicateUserException e) {
    log.warn("중복된 사용자: {}", e.getMessage());
    return ResponseEntity
        .status(HttpStatus.CONFLICT)
        .body("이미 존재하는 사용자입니다.");
  }

  @ExceptionHandler(CustomException.InvalidPasswordException.class)
  public ResponseEntity<String> handleInvalidPasswordException(CustomException.InvalidPasswordException e) {
    log.warn("잘못된 비밀번호: {}", e.getMessage());
    return ResponseEntity
        .status(HttpStatus.UNAUTHORIZED)
        .body("잘못된 비밀번호입니다.");
  }

  // === 사용자 상태 관련 예외들 ===

  @ExceptionHandler(CustomException.InvalidUserStatusException.class)
  public ResponseEntity<String> handleInvalidUserStatusException(CustomException.InvalidUserStatusException e) {
    log.warn("잘못된 사용자 상태: {}", e.getMessage());
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body("잘못된 사용자 상태입니다.");
  }

  @ExceptionHandler(CustomException.DuplicateUserStatusException.class)
  public ResponseEntity<String> handleDuplicateUserStatusException(CustomException.DuplicateUserStatusException e) {
    log.warn("중복된 사용자 상태: {}", e.getMessage());
    return ResponseEntity
        .status(HttpStatus.CONFLICT)
        .body("이미 존재하는 사용자 상태입니다.");
  }

  // === 채널 관련 예외들 ===

  @ExceptionHandler(CustomException.ChannelNotFoundException.class)
  public ResponseEntity<String> handleChannelNotFoundException(CustomException.ChannelNotFoundException e) {
    log.warn("채널을 찾을 수 없음: {}", e.getMessage());
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body("채널 정보를 찾을 수 없습니다.");
  }

  @ExceptionHandler(CustomException.PrivateChannelUpdateException.class)
  public ResponseEntity<String> handlePrivateChannelUpdateException(CustomException.PrivateChannelUpdateException e) {
    log.warn("비공개 채널 수정 시도: {}", e.getMessage());
    return ResponseEntity
        .status(HttpStatus.FORBIDDEN)
        .body("비공개 채널은 수정할 수 없습니다.");
  }

  // === 메시지 관련 예외들 ===

  @ExceptionHandler(CustomException.MessageNotFoundException.class)
  public ResponseEntity<String> handleMessageNotFoundException(CustomException.MessageNotFoundException e) {
    log.warn("메시지를 찾을 수 없음: {}", e.getMessage());
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body("메시지를 찾을 수 없습니다.");
  }

  // === 읽기 상태 관련 예외들 ===

  @ExceptionHandler(CustomException.ReadStatusNotFoundException.class)
  public ResponseEntity<String> handleReadStatusNotFoundException(CustomException.ReadStatusNotFoundException e) {
    log.warn("읽기 상태를 찾을 수 없음: {}", e.getMessage());
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body("읽기 상태를 찾을 수 없습니다.");
  }

  @ExceptionHandler(CustomException.DuplicateReadStatusException.class)
  public ResponseEntity<String> handleDuplicateReadStatusException(CustomException.DuplicateReadStatusException e) {
    log.warn("중복된 읽기 상태: {}", e.getMessage());
    return ResponseEntity
        .status(HttpStatus.CONFLICT)
        .body("이미 존재하는 읽기 상태입니다.");
  }

  // === 바이너리 콘텐츠 관련 예외들 ===

  @ExceptionHandler(CustomException.BinaryContentNotFoundException.class)
  public ResponseEntity<String> handleBinaryContentNotFoundException(CustomException.BinaryContentNotFoundException e) {
    log.warn("바이너리 콘텐츠를 찾을 수 없음: {}", e.getMessage());
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body("파일을 찾을 수 없습니다.");
  }

  // === 기본 예외 처리 ===

  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleGenericException(Exception e) {
    log.error("예상치 못한 오류 발생: {}", e.getMessage(), e);
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body("서버 내부 오류가 발생했습니다.");
  }
}
