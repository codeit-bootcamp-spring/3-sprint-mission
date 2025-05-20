package com.sprint.mission.discodeit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.NoSuchElementException;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(NullPointerException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<String> nullPointerExceptionHandler(NullPointerException exception) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("올바르지 않은 참조입니다.");
  }

  @ExceptionHandler(NoSuchElementException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<String> noSuchElementExceptionHandler(NoSuchElementException e) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("존재하지 않는 값 입니다.");
  }

  //default 처리
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<String> defaultExceptionHandler(Exception exception) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body("알수없는 오류가 발생했습니다. 다시 시도해 주세요.");
  }
}