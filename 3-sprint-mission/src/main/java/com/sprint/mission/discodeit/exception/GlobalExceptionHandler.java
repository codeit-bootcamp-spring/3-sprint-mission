package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  private HttpStatus determineHttpStatus(DiscodeitException e) {
    ErrorCode errorCode = e.getErrorCode();
    return switch (errorCode) {
      case USER_NOT_FOUND, CHANNEL_NOT_FOUND, MESSAGE_NOT_FOUND, BINARY_CONTENT_NOT_FOUND,
           READ_STATUS_NOT_FOUND, USER_STATUS_NOT_FOUND -> HttpStatus.NOT_FOUND;
      case DUPLICATE_USER, DUPLICATE_READ_STATUS, DUPLICATE_USER_STATUS -> HttpStatus.CONFLICT;
      case USER_PASSWORD_NOT_MATCHED -> HttpStatus.UNAUTHORIZED;
      case PRIVATE_CHANNEL_UPDATE, INVALID_REQUEST, VALIDATION_ERROR -> HttpStatus.BAD_REQUEST;
      case INTERNAL_SERVER_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
    };
  }

  @ExceptionHandler(DiscodeitException.class)
  public ResponseEntity<ErrorResponse> handleDiscodeitException(DiscodeitException e) {
    log.error("커스텀 예외 발생: code={}, message={}", e.getErrorCode(), e.getMessage(), e);
    HttpStatus status = determineHttpStatus(e);
    ErrorResponse errorResponse = new ErrorResponse(e, status.value());
    return ResponseEntity
        .status(status)
        .body(errorResponse);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationExceptions(
      MethodArgumentNotValidException e) {
    log.error("요청 유효성 검사 실패: {}", e.getMessage(), e);

    Map<String, Object> validationErrors = new HashMap<>();
    e.getBindingResult().getFieldErrors().forEach(fieldError -> {
      String fieldName = ((FieldError) fieldError).getField();
      String errorMessage = fieldError.getDefaultMessage();
      validationErrors.put(fieldName, errorMessage);
    });

    ErrorResponse errorResponse = new ErrorResponse(
        Instant.now(),
        ErrorCode.VALIDATION_ERROR.getClass().getSimpleName(),
        ErrorCode.VALIDATION_ERROR.getMessage(),
        validationErrors,
        e.getClass().getSimpleName(),
        HttpStatus.BAD_REQUEST.value()
    );

    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(errorResponse);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception e) {
    log.error("예상치 못한 오류 발생: {}", e.getMessage(), e);
    ErrorResponse errorResponse = new ErrorResponse(e, HttpStatus.INTERNAL_SERVER_ERROR.value());
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(errorResponse);
  }
}
