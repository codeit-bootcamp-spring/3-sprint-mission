package com.sprint.mission.discodeit.exception;

import java.io.IOException;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(NullPointerException.class)
  public ResponseEntity<String> handleException(NullPointerException e) {
    log.error("NullPointerException: {}", e.getMessage(), e);
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body("값이 존재하지 않습니다." + e.getMessage());
  }

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<String> handleException(NoSuchElementException e) {
    log.error("NoSuchElementException: {}", e.getMessage(), e);
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body("해당 데이터가 존재하지 않습니다." + e.getMessage());
  }

  @ExceptionHandler(IllegalAccessException.class)
  public ResponseEntity<String> handleException(IllegalAccessException e) {
    log.error("IllegalAccessException: {}", e.getMessage(), e);
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body("접근 권한이 없습니다." + e.getMessage());
  }

  @ExceptionHandler(ClassNotFoundException.class)
  public ResponseEntity<String> handleException(ClassNotFoundException e) {
    log.error("ClassNotFoundException: {}", e.getMessage(), e);
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body("파일을 찾을 수 없습니다." + e.getMessage());
  }

  @ExceptionHandler(IOException.class)
  public ResponseEntity<String> handleException(IOException e) {
    log.error("IOException: {}", e.getMessage(), e);
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body("파일을 찾을 수 없습니다." + e.getMessage());
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<String> handleException(RuntimeException e) {
    log.error("RuntimeException: {}", e.getMessage(), e);
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body("일시적인 오류가 발생했습니다.");
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleException(Exception e) {
    log.error("Exception: {}", e.getMessage(), e);
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body("일시적인 오류가 발생했습니다.");
  }
}
