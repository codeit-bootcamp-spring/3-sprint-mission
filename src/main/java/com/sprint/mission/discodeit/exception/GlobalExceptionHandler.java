package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.dto.response.ErrorResponse;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DiscodeitException.class)
    public ResponseEntity<ErrorResponse> handleCommonException(DiscodeitException e) {
        ErrorCode error = e.getErrorCode();

        ErrorResponse response = ErrorResponse.builder()
            .timestamp(Instant.now())
            .code(error.getCode())
            .message(error.getMessage())
            .status(error.getStatus())
            .exceptionType(e.getClass().getSimpleName())
            .details(e.getDetails())
            .build();

        return ResponseEntity
            .status(error.getStatus())
            .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
        ErrorCode error = ErrorCode.UNKNOWN_ERROR;

        return ResponseEntity
            .status(error.getStatus())
            .body(ErrorResponse.of(error));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
        MethodArgumentNotValidException e) {
        Map<String, Object> details = e.getBindingResult()
            .getFieldErrors().stream()
            .collect(Collectors.toMap(
                FieldError::getField,
                err -> Optional.ofNullable(err.getDefaultMessage()).orElse("유효성 오류"),
                (existing, replacement) -> existing
            ));

        ErrorResponse error = ErrorResponse.builder()
            .timestamp(Instant.now())
            .code("VALIDATION_FAILED")
            .message("요청 데이터 유효성 검증에 실패하였습니다.")
            .status(HttpStatus.BAD_REQUEST.value())
            .exceptionType(e.getClass().getSimpleName())
            .details(details)
            .build();

        return ResponseEntity
            .status(error.getStatus())
            .body(error);
    }
}
