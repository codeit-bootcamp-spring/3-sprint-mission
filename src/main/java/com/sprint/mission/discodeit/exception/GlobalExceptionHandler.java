package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.dto.response.ErrorResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private ResponseEntity<ErrorResponse> sendErrorResponse(HttpStatus status, String message) {
    return ResponseEntity.status(status).body(ErrorResponse.of(status, message));
  }

  // 잘못된 요청을 하는 경우
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleException(IllegalArgumentException e) {

    return sendErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
  }

  // 데이터가 존재하지 않을 경우
  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<ErrorResponse> handleException(NoSuchElementException e) {

    return sendErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
  }

  // 실행 도중에 문제가 생겼을 경우
  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ErrorResponse> runtimeExceptionExceptionHandler(RuntimeException e) {
    return sendErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
  }

  // 나머지 예외 처리
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception e) {

    return sendErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
  }

  //유효성 검증 오류 시 예외 처리
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationExceptions(
      MethodArgumentNotValidException e) {
    String message = e.getBindingResult().getFieldErrors().stream()
        .map(error -> error.getField() + ": " + error.getDefaultMessage())
        .collect(Collectors.joining(", "));
    return sendErrorResponse(HttpStatus.BAD_REQUEST, message);
  }

}
