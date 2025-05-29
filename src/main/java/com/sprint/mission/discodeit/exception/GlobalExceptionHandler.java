package com.sprint.mission.discodeit.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<String> handleException(IllegalArgumentException e) {
    logger.warn("잘못된 요청 파라미터: ", e.getMessage(), e);
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body("잘못된 요청방식입니다.");
  }

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<String> handleException(NoSuchElementException e) {
    logger.warn("리소스를 찾을 수 없음: ", e.getMessage(), e);
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body("정보를 찾을 수 없습니다.");
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleException(Exception e) {
    logger.error("예상치 못한 오류 발생: {}", e.getMessage(), e);
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body("알 수 없는 오류가 발생했습니다.");
  }

  // 사용자 관련 예외들
  @ExceptionHandler(CustomException.UserNotFoundException.class)
  public ResponseEntity<String> handleException(CustomException.UserNotFoundException e) {
    logger.warn("사용자를 찾을 수 없음: {}", e.getMessage(), e);
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body("사용자 정보를 찾을 수 없습니다.");
  }

  @ExceptionHandler(CustomException.DuplicateUserException.class)
  public ResponseEntity<String> handleException(CustomException.DuplicateUserException e) {
    logger.warn("중복된 사용자: {}", e.getMessage(), e);
    return ResponseEntity
        .status(HttpStatus.CONFLICT)
        .body("이미 존재하는 사용자입니다.");
  }

  @ExceptionHandler(CustomException.InvalidPasswordException.class)
  public ResponseEntity<String> handleException(CustomException.InvalidPasswordException e) {
    logger.warn("잘못된 비밀번호: {}", e.getMessage(), e);
    return ResponseEntity
        .status(HttpStatus.UNAUTHORIZED)
        .body("잘못된 비밀번호입니다.");
  }

  // 사용자 상태 관련 예외들
  @ExceptionHandler(CustomException.InvalidUserStatusException.class)
  public ResponseEntity<String> handleException(CustomException.InvalidUserStatusException e) {
    logger.warn("잘못된 사용자 상태: {}", e.getMessage(), e);
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body("잘못된 사용자 상태입니다.");
  }

  @ExceptionHandler(CustomException.DuplicateUserStatusException.class)
  public ResponseEntity<String> handleException(CustomException.DuplicateUserStatusException e) {
    logger.warn("중복된 사용자 상태: {}", e.getMessage(), e);
    return ResponseEntity
        .status(HttpStatus.CONFLICT)
        .body("이미 존재하는 사용자 상태입니다.");
  }

  // 채널 관련 예외들
  @ExceptionHandler(CustomException.ChannelNotFoundException.class)
  public ResponseEntity<String> handleException(CustomException.ChannelNotFoundException e) {
    logger.warn("채널을 찾을 수 없음: {}", e.getMessage(), e);
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body("채널 정보를 찾을 수 없습니다.");
  }

  @ExceptionHandler(CustomException.PrivateChannelUpdateException.class)
  public ResponseEntity<String> handleException(CustomException.PrivateChannelUpdateException e) {
    logger.warn("프라이빗 채널 수정 시도: {}", e.getMessage(), e);
    return ResponseEntity
        .status(HttpStatus.FORBIDDEN)
        .body("프라이빗 채널은 수정할 수 없습니다.");
  }

  // 메시지 관련 예외들
  @ExceptionHandler(CustomException.MessageNotFoundException.class)
  public ResponseEntity<String> handleException(CustomException.MessageNotFoundException e) {
    logger.warn("메시지를 찾을 수 없음: {}", e.getMessage(), e);
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body("메시지를 찾을 수 없습니다.");
  }

  // 읽기 상태 관련 예외들
  @ExceptionHandler(CustomException.ReadStatusNotFoundException.class)
  public ResponseEntity<String> handleException(CustomException.ReadStatusNotFoundException e) {
    logger.warn("읽기 상태를 찾을 수 없음: {}", e.getMessage(), e);
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body("읽기 상태를 찾을 수 없습니다.");
  }

  @ExceptionHandler(CustomException.DuplicateReadStatusException.class)
  public ResponseEntity<String> handleException(CustomException.DuplicateReadStatusException e) {
    logger.warn("중복된 읽기 상태: {}", e.getMessage(), e);
    return ResponseEntity
        .status(HttpStatus.CONFLICT)
        .body("이미 존재하는 읽기 상태입니다.");
  }

  // 바이너리 콘텐츠 관련 예외들
  @ExceptionHandler(CustomException.BinaryContentNotFoundException.class)
  public ResponseEntity<String> handleException(CustomException.BinaryContentNotFoundException e) {
    logger.warn("바이너리 콘텐츠를 찾을 수 없음: {}", e.getMessage(), e);
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body("파일을 찾을 수 없습니다.");
  }
}
