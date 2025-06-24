package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentException;
import com.sprint.mission.discodeit.exception.channel.ChannelException;
import com.sprint.mission.discodeit.exception.message.MessageException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusException;
import com.sprint.mission.discodeit.exception.user.UserException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.validation.FieldError;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 애플리케이션 전역에서 발생하는 예외를 처리하는 핸들러입니다.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends RuntimeException {

    /**
     * User 도메인 예외를 처리합니다.
     */
    @ExceptionHandler(UserException.class)
    public ResponseEntity<ErrorResponse> handleUserException(UserException ex) {
        ErrorResponse response = new ErrorResponse(
            ex.getErrorCode().getStatus().value(),
            ex.getErrorCode().getCode(),
            ex.getMessage(),
            ex.getTimestamp(),
            ex.getDetails()
        );
        return ResponseEntity.status(ex.getErrorCode().getStatus()).body(response);
    }

    /**
     * Channel 도메인 예외를 처리합니다.
     */
    @ExceptionHandler(ChannelException.class)
    public ResponseEntity<ErrorResponse> handleChannelException(ChannelException ex) {
        ErrorResponse response = new ErrorResponse(
            ex.getErrorCode().getStatus().value(),
            ex.getErrorCode().getCode(),
            ex.getMessage(),
            ex.getTimestamp(),
            ex.getDetails()
        );
        return ResponseEntity.status(ex.getErrorCode().getStatus()).body(response);
    }

    /**
     * Message 도메인 예외를 처리합니다.
     */
    @ExceptionHandler(MessageException.class)
    public ResponseEntity<ErrorResponse> handleMessageException(MessageException ex) {
        ErrorResponse response = new ErrorResponse(
            ex.getErrorCode().getStatus().value(),
            ex.getErrorCode().getCode(),
            ex.getMessage(),
            ex.getTimestamp(),
            ex.getDetails()
        );
        return ResponseEntity.status(ex.getErrorCode().getStatus()).body(response);
    }

    /**
     * ReadStatus 도메인 예외를 처리합니다.
     */
    @ExceptionHandler(ReadStatusException.class)
    public ResponseEntity<ErrorResponse> handleReadStatusException(ReadStatusException ex) {
        ErrorResponse response = new ErrorResponse(
            ex.getErrorCode().getStatus().value(),
            ex.getErrorCode().getCode(),
            ex.getMessage(),
            ex.getTimestamp(),
            ex.getDetails()
        );
        return ResponseEntity.status(ex.getErrorCode().getStatus()).body(response);
    }

    /**
     * BinaryContent 도메인 예외를 처리합니다.
     */
    @ExceptionHandler(BinaryContentException.class)
    public ResponseEntity<ErrorResponse> handleBinaryContentException(BinaryContentException ex) {
        ErrorResponse response = new ErrorResponse(
            ex.getErrorCode().getStatus().value(),
            ex.getErrorCode().getCode(),
            ex.getMessage(),
            ex.getTimestamp(),
            ex.getDetails()
        );
        return ResponseEntity.status(ex.getErrorCode().getStatus()).body(response);
    }

    /**
     * UserStatus 도메인 예외를 처리합니다.
     */
    @ExceptionHandler(UserStatusException.class)
    public ResponseEntity<ErrorResponse> handleUserStatusException(UserStatusException ex) {
        ErrorResponse response = new ErrorResponse(
                ex.getErrorCode().getStatus().value(),
                ex.getErrorCode().getCode(),
                ex.getMessage(),
                ex.getTimestamp(),
                ex.getDetails()
        );
        return ResponseEntity.status(ex.getErrorCode().getStatus()).body(response);
    }

    /**
     * @Valid 검증 실패 시 발생하는 예외를 처리합니다.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining(", "));
        
        ErrorResponse response = new ErrorResponse(
            ErrorCode.METHOD_ARGUE_NOT_VALID.getStatus().value(),
            ErrorCode.METHOD_ARGUE_NOT_VALID.getCode(),
            errorMessage,
            java.time.Instant.now(),
            Map.of("validationErrors", ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                    FieldError::getField,
                    FieldError::getDefaultMessage
                )))
        );
        return ResponseEntity.status(ErrorCode.METHOD_ARGUE_NOT_VALID.getStatus()).body(response);
    }
}
