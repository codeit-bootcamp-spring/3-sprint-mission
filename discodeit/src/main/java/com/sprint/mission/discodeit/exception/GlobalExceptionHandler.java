package com.sprint.mission.discodeit.exception;


import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

  // 이미 존재하는 데이터가 있는 경우
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<String> illegalExceptionHandler(IllegalArgumentException e) {
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(e.getMessage());
  }

  // 파일 입출력 실패
  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<String> noSuchExceptionHandler(NoSuchElementException e) {
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(e.getMessage());
  }

  // Default
  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> exceptionHandler(Exception e) {
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(e.getMessage());
  }
}
