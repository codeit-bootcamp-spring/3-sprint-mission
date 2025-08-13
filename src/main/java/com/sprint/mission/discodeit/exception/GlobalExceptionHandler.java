package com.sprint.mission.discodeit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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

  // === DiscodeitException 통합 처리 ===

  /**
   * 모든 DiscodeitException의 통합 처리기
   * 모든 커스텀 예외를 일관되게 처리합니다.
   */
  @ExceptionHandler(DiscodeitException.class)
  public ResponseEntity<ErrorResponse> handleDiscodeitException(DiscodeitException e) {
    // 로그 레벨을 ErrorCode의 HTTP 상태에 따라 결정
    HttpStatus status = e.getErrorCode().getHttpStatus();
    if (status.is5xxServerError()) {
      log.error("서버 오류 발생: {} - {}", e.getErrorCode(), e.getMessage(), e);
    } else if (status.is4xxClientError()) {
      log.warn("클라이언트 오류: {} - {}", e.getErrorCode(), e.getMessage());
    } else {
      log.info("예외 발생: {} - {}", e.getErrorCode(), e.getMessage());
    }

    ErrorResponse errorResponse = ErrorResponse.of(e);

    return ResponseEntity
        .status(status)
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

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(
      MissingServletRequestParameterException e) {
    log.warn("필수 요청 파라미터 누락: {} (타입: {})", e.getParameterName(), e.getParameterType());

    ErrorResponse errorResponse = ErrorResponse.of(
        HttpStatus.BAD_REQUEST.value(),
        e.getClass().getSimpleName(),
        String.format("필수 파라미터가 누락되었습니다: %s", e.getParameterName()));

    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(errorResponse);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
      MethodArgumentTypeMismatchException e) {
    log.warn("파라미터 타입 변환 실패: {} = '{}' (기대 타입: {})",
        e.getName(), e.getValue(), e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "unknown");

    String message = String.format("파라미터 '%s'의 값이 올바르지 않습니다: %s", e.getName(), e.getValue());

    ErrorResponse errorResponse = ErrorResponse.of(
        HttpStatus.BAD_REQUEST.value(),
        e.getClass().getSimpleName(),
        message);

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
