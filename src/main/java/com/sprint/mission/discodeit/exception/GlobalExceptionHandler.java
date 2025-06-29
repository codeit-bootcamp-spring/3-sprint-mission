package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DiscodeitException.class)
    public ResponseEntity<ErrorResponse> handleDiscodeitException(DiscodeitException ex) {
        ErrorCode code = ex.getErrorCode();
        HttpStatus status = HttpStatus.BAD_REQUEST;
        if (code == ErrorCode.USER_NOT_FOUND || code == ErrorCode.CHANNEL_NOT_FOUND || code == ErrorCode.MESSAGE_NOT_FOUND) {
            status = HttpStatus.NOT_FOUND;
        }
        ErrorResponse response = new ErrorResponse(
            status.value(),
            ex.getClass().getSimpleName(),
            code.getCode(),
            code.getMessage(),
            ex.getDetails()
        );
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String detailMessage = ex.getBindingResult().getFieldErrors().stream()
            .findFirst()
            .map(error -> String.format("%s: %s", error.getField(), error.getDefaultMessage()))
            .orElse("입력값이 올바르지 않습니다.");

        ErrorResponse response = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            ex.getClass().getSimpleName(),
            "VALIDATION_ERROR",
            "요청 값이 유효하지 않습니다.",
            detailMessage
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        ErrorResponse response = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            ex.getClass().getSimpleName(),
            "INTERNAL_ERROR",
            "서버 내부 오류가 발생했습니다.",
            null
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}