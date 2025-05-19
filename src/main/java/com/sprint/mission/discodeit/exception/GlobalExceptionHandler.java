package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.dto.Common.ApiErrorResponse;
import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ApiErrorResponse> toErrorResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(ApiErrorResponse.of(status, message));
    }

    // 이미 존재하는 데이터가 있는 경우
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorResponse> illegalStateExceptionHandler(IllegalStateException e) {
        return toErrorResponse(HttpStatus.CONFLICT, e.getMessage());
    }

    // 데이터가 존재하지 않는 경우
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiErrorResponse> noSuchElementExceptionHandler(
            NoSuchElementException e) {
        return toErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
    }

    // 잘못된 요청
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> badRequestExceptionHandler(IllegalArgumentException e) {
        return toErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    // 파일 입출력 실패
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiErrorResponse> runtimeExceptionExceptionHandler(RuntimeException e) {
        return toErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    // Default 에러 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> exceptionHandler(Exception e) {
        return toErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
}
