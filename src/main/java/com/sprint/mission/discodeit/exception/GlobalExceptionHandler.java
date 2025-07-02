
package com.sprint.mission.discodeit.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DiscodeitException.class)
    public ResponseEntity<ErrorResponse> handleDiscodeitException(DiscodeitException e) {
        log.warn("DiscodeitException 발생: {}", e.getMessage());
        ErrorResponse response = ErrorResponse.builder()
            .status(e.getErrorCode().getStatus().value())
            .message(e.getErrorCode().getMessage())
            .exceptionType(e.getClass().getSimpleName())
            .details(e.getDetails())
            .build();
        return ResponseEntity.status(e.getErrorCode().getStatus()).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
        String details = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        ErrorResponse response = ErrorResponse.builder()
            .status(400)
            .message("유효성 검증 실패")
            .exceptionType("MethodArgumentNotValidException")
            .details(details)
            .build();
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraint(ConstraintViolationException e) {
        ErrorResponse response = ErrorResponse.builder()
            .status(400)
            .message("제약 조건 위반")
            .exceptionType("ConstraintViolationException")
            .details(e.getMessage())
            .build();
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception e) {
        log.error("예외 발생", e);
        ErrorResponse response = ErrorResponse.builder()
            .status(500)
            .message("알 수 없는 서버 오류가 발생했습니다.")
            .exceptionType(e.getClass().getSimpleName())
            .details(e.getMessage())
            .build();
        return ResponseEntity.internalServerError().body(response);
    }
}
