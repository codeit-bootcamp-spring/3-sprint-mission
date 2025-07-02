package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.exception.BinaryContent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.exception.Channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.Channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.exception.Message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.ReadStatus.ReadStatusAlreadyExistsException;
import com.sprint.mission.discodeit.exception.ReadStatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.User.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.User.UserNotFoundException;
import com.sprint.mission.discodeit.exception.User.UserPasswordMismatchException;
import com.sprint.mission.discodeit.exception.UserStatus.UserStatusAlreadyExistsException;
import com.sprint.mission.discodeit.exception.UserStatus.UserStatusNotFoundException;
import java.time.Instant;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ErrorResponse> toErrorResponse(HttpStatus httpStatus, Throwable error) {
        if (error instanceof DiscodeitException discodeitException) {
            return ResponseEntity.status(httpStatus)
                    .body(ErrorResponse.of(httpStatus, discodeitException));
        }

        String message = error.getMessage() != null ? error.getMessage() : "Unknown Error";

        if (error instanceof MethodArgumentNotValidException methodArgumentNotValidException) {
            FieldError fieldError = methodArgumentNotValidException.getFieldError();
            message = fieldError != null ? fieldError.getDefaultMessage() : "잘못된 요청입니다.";
        }

        return ResponseEntity.status(httpStatus)
                .body(new ErrorResponse(
                        Instant.now(),
                        httpStatus.getReasonPhrase(),
                        message,
                        Map.of(),
                        error.getClass().getSimpleName(),
                        httpStatus.value())
                );
    }

    // User
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException e) {
        log.warn("User not found: {}", e.getMessage());
        return toErrorResponse(HttpStatus.NOT_FOUND, e);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(
            UserAlreadyExistsException e) {
        log.warn("User already exists: {}", e.getMessage());
        return toErrorResponse(HttpStatus.CONFLICT, e);
    }

    @ExceptionHandler(UserPasswordMismatchException.class)
    public ResponseEntity<ErrorResponse> handleUserPasswordMismatchException(
            UserPasswordMismatchException e) {
        log.warn("User password mismatch: {}", e.getMessage());
        return toErrorResponse(HttpStatus.UNAUTHORIZED, e);
    }

    // Channel
    @ExceptionHandler(ChannelNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleChannelNotFoundException(
            ChannelNotFoundException e) {
        log.warn("Channel not found: {}", e.getMessage());
        return toErrorResponse(HttpStatus.NOT_FOUND, e);
    }

    @ExceptionHandler(PrivateChannelUpdateException.class)
    public ResponseEntity<ErrorResponse> handlePrivateChannelUpdateException(
            PrivateChannelUpdateException e) {
        log.warn("Private channel update failed: {}", e.getMessage());
        return toErrorResponse(HttpStatus.CONFLICT, e);
    }

    // Message
    @ExceptionHandler(MessageNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleMessageNotFoundException(
            MessageNotFoundException e) {
        log.warn("Message not found: {}", e.getMessage());
        return toErrorResponse(HttpStatus.NOT_FOUND, e);
    }

    // BinaryContent
    @ExceptionHandler(BinaryContentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBinaryContentNotFoundException(
            BinaryContentNotFoundException e) {
        log.warn("Binary content not found: {}", e.getMessage());
        return toErrorResponse(HttpStatus.NOT_FOUND, e);
    }

    // ReadStatus
    @ExceptionHandler(ReadStatusNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleReadStatusNotFoundException(
            ReadStatusNotFoundException e) {
        log.warn("Read status not found: {}", e.getMessage());
        return toErrorResponse(HttpStatus.NOT_FOUND, e);
    }

    @ExceptionHandler(ReadStatusAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleReadStatusAlreadyExistsException(
            ReadStatusAlreadyExistsException e) {
        log.warn("Read status already exists: {}", e.getMessage());
        return toErrorResponse(HttpStatus.CONFLICT, e);
    }

    // UserStatus
    @ExceptionHandler(UserStatusNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserStatusNotFoundException(
            UserStatusNotFoundException e) {
        log.warn("User status not found: {}", e.getMessage());
        return toErrorResponse(HttpStatus.NOT_FOUND, e);
    }

    @ExceptionHandler(UserStatusAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserStatusAlreadyExistsException(
            UserStatusAlreadyExistsException e) {
        log.warn("User status already exists: {}", e.getMessage());
        return toErrorResponse(HttpStatus.CONFLICT, e);
    }

    // 입력값 유효성 검증 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {
        log.warn("MethodArgumentNotValidException: {}", e.getMessage());
        return toErrorResponse(HttpStatus.BAD_REQUEST, e);
    }

    // Default 에러 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> exceptionHandler(Exception e) {
        log.error("Exception 발생: {}", e.getMessage());
        return toErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }


}
