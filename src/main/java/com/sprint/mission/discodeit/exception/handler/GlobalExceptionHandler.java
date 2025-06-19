package com.sprint.mission.discodeit.exception.handler;

import com.sprint.mission.discodeit.dto.response.ErrorResponse;
import com.sprint.mission.discodeit.entity.ErrorCode;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.binarycontent.NotFoundBinaryContentException;
import com.sprint.mission.discodeit.exception.channel.NotFoundChannelException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.exception.message.NotFoundMessageException;
import com.sprint.mission.discodeit.exception.readstatus.NotFoundReadStatusException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.DuplicateEmailException;
import com.sprint.mission.discodeit.exception.user.DuplicateNameException;
import com.sprint.mission.discodeit.exception.user.LoginFailedException;
import com.sprint.mission.discodeit.exception.user.NotFoundUserException;
import com.sprint.mission.discodeit.exception.userstatus.NotFoundUserStatusException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({NotFoundUserException.class, NotFoundChannelException.class,
            NotFoundMessageException.class, NotFoundBinaryContentException.class,
            NotFoundUserStatusException.class, NotFoundReadStatusException.class})
    public ResponseEntity<ErrorResponse> handleNotFoundException(DiscodeitException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                Instant.now(),
                e.getErrorCode().toString(),
                e.getMessage(),
                e.getDetails(),
                e.getClass().getTypeName(),
                HttpStatus.NOT_FOUND.value()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler({DuplicateEmailException.class, DuplicateNameException.class,
            UserStatusAlreadyExistsException.class, ReadStatusAlreadyExistsException.class,
            PrivateChannelUpdateException.class})
    public ResponseEntity<ErrorResponse> handleBadRequestException(DiscodeitException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                Instant.now(),
                e.getErrorCode().toString(),
                e.getMessage(),
                e.getDetails(),
                e.getClass().getTypeName(),
                HttpStatus.BAD_REQUEST.value()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(LoginFailedException.class)
    public ResponseEntity<ErrorResponse> handleLoginFailedException(DiscodeitException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                Instant.now(),
                e.getErrorCode().toString(),
                e.getMessage(),
                e.getDetails(),
                e.getClass().getTypeName(),
                HttpStatus.UNAUTHORIZED.value()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    // @Valid 검증 실패 시 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();

        Map<String, Object> details = new HashMap<>();
        for (FieldError fieldError : fieldErrors) {
            details.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        ErrorResponse errorResponse = new ErrorResponse(
                Instant.now(),
                "VALIDATION_FAILED",
                "유효성 검사 실패",
                details,
                e.getClass().getTypeName(),
                HttpStatus.BAD_REQUEST.value()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /* 정의되지 않은 예외 처리 */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleUnhandledException(Exception e) {
        ErrorResponse errorResponse = new ErrorResponse(
                Instant.now(),
                ErrorCode.INTERNAL_SERVER_ERROR.toString(),
                e.getMessage(),
                null,
                e.getClass().getTypeName(),
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
