package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * packageName    : com.sprint.mission.discodeit.exception
 * fileName       : GlobalExceptionHandler
 * author         : doungukkim
 * date           : 2025. 5. 12.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 5. 12.        doungukkim       최초 생성
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class) // 400
    public ResponseEntity<?> IllegalArgumentExceptionHandler(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class) // 404
    public ResponseEntity<?> NoSuchElementExceptionHandler(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(Exception.class) // 500
    public ResponseEntity<?> ExceptionHandler(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    //0 여기 아래로는 바뀐 요구사항
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> userNotFoundExceptionHandler(UserNotFoundException e) {
        return buildDiscodeitException(e);
    }
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> userAlreadyExistsExceptionHandler(UserAlreadyExistsException e) {
        return buildDiscodeitException(e);
    }
    @ExceptionHandler(ChannelNotFoundException.class)
    public ResponseEntity<ErrorResponse> channelNotFoundExceptionHandler(ChannelNotFoundException e) {
        return buildDiscodeitException(e);
    }
    @ExceptionHandler(PrivateChannelUpdateException.class)
    public ResponseEntity<ErrorResponse> privateChannelUpdateExceptionHandler(PrivateChannelUpdateException e) {
        return buildDiscodeitException(e);
    }
    @ExceptionHandler(MessageNotFoundException.class)
    public ResponseEntity<ErrorResponse> messageNotFoundExceptionHandler(MessageNotFoundException e) {
        return buildDiscodeitException(e);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        Map<String,Object> details = new HashMap<>();
        details.put(e.getFieldError().getField(),e.getFieldError().getDefaultMessage());

        ErrorResponse response = ErrorResponse.builder()
            .timestamp(Instant.now())
            .code(ErrorCode.VALIDATION_FAILED.toString())
            .message(e.getMessage())
            .details(details) // nullable
            .exceptionType(e.getClass().getSimpleName())
            .status(ErrorCode.VALIDATION_FAILED.getStatus().value())
            .build();
        return ResponseEntity.status(ErrorCode.VALIDATION_FAILED.getStatus()).body(response);
    }

    private ResponseEntity<ErrorResponse> buildDiscodeitException(DiscodeitException e) {
        ErrorCode errorCode = e.getErrorCode();
        ErrorResponse response = ErrorResponse.builder()
            .timestamp(Instant.now())
            .code(e.getErrorCode().toString())
            .message(e.getMessage())
            .details(e.getDetails()) // nullable
            .exceptionType(e.getClass().getSimpleName())
            .status(errorCode.getStatus().value())
            .build();
        return ResponseEntity.status(e.getErrorCode().getStatus()).body(response);
    }
}