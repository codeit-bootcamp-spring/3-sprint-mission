package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException e) {
        return error(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(NoSuchElementException e) {
        return error(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleAll(Exception e) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "서버에 오류가 발생했습니다.");
    }

    private ResponseEntity<Map<String, String>> error(HttpStatus status, String message) {
        Map<String, String> body = new HashMap<>();
        body.put("error", message);
        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(DiscodeitException.class)
    public ResponseEntity<ErrorResponse> handleDiscodeitException(DiscodeitException exception) {
        log.error("커스텀 예외 발생: code={}, message={}", exception.getErrorCode(), exception.getMessage(), exception);
        HttpStatus status = determineHttpStatus(exception);
        ErrorResponse response = new ErrorResponse(exception, status.value());
        return ResponseEntity
            .status(status)
            .body(response);
    }

    private HttpStatus determineHttpStatus(DiscodeitException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        return switch (errorCode) {
            case USER_NOT_FOUND, CHANNEL_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case DUPLICATE_USER -> HttpStatus.CONFLICT;
            case PRIVATE_CHANNEL_UPDATE -> HttpStatus.BAD_REQUEST;
        };
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("요청 유효성 검사 실패: {}", ex.getMessage());

        Map<String, Object> validationErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationErrors.put(fieldName, errorMessage);
        });

        ErrorResponse response = new ErrorResponse(
            Instant.now(),
            "VALIDATION_ERROR",
            "요청 데이터 유효성 검사에 실패했습니다",
            validationErrors,
            ex.getClass().getSimpleName(),
            HttpStatus.BAD_REQUEST.value()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
