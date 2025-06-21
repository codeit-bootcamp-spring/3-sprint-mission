package com.sprint.mission.discodeit.exception;

import java.io.IOException;
import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(NullPointerException.class)
  public ResponseEntity<String> handleException(NullPointerException e) {
    e.printStackTrace();
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body("값이 존재하지 않습니다." + e.getMessage());
  }

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<String> handleException(NoSuchElementException e) {
    e.printStackTrace();
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body("해당 데이터가 존재하지 않습니다." + e.getMessage());
  }

  @ExceptionHandler(IllegalAccessException.class)
  public ResponseEntity<String> handleException(IllegalAccessException e) {
    e.printStackTrace();
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body("접근 권한이 없습니다." + e.getMessage());
  }

  @ExceptionHandler(ClassNotFoundException.class)
  public ResponseEntity<String> handleException(ClassNotFoundException e) {
    e.printStackTrace();
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body("파일을 찾을 수 없습니다." + e.getMessage());
  }

  @ExceptionHandler(IOException.class)
  public ResponseEntity<String> handleException(IOException e) {
    e.printStackTrace();
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body("파일을 찾을 수 없습니다." + e.getMessage());
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<String> handleException(RuntimeException e) {
    e.printStackTrace();
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body("일시적인 오류가 발생했습니다.");
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleException(Exception e) {
    e.printStackTrace();
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body("일시적인 오류가 발생했습니다.");
  }
}
