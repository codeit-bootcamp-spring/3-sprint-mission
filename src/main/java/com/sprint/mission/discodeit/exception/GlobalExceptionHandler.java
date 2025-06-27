package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.dto.response.ErrorResponse;
import com.sprint.mission.discodeit.exception.channel.ChannelAlreadyExistException;
import java.nio.charset.CharacterCodingException;
import java.time.Instant;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.springframework.boot.autoconfigure.integration.IntegrationProperties.Error;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 커스텀 예외 처리
     */
    @ExceptionHandler(DiscodeitException.class)
    public ResponseEntity<ErrorResponse> handleException(DiscodeitException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getTimestamp(),
            e.getErrorCode().getCode(),
            e.getErrorCode().getMessage(), e.getDetails(), e.getClass().getSimpleName(),
            e.getErrorCode().getHttpStatus().value());

        return ResponseEntity
            .status(e.getErrorCode().getHttpStatus())
            .body(errorResponse);
    }

    // @Valid 실패 시 자동 호출됨
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleException(
        MethodArgumentNotValidException e) {

        // validation 에러를 정리해서 응답
        Map<String, Object> errors = e.getBindingResult()
            .getFieldErrors()
            .stream()
            .collect(Collectors.toMap(
                FieldError::getField,
                FieldError::getDefaultMessage
            ));

        ErrorCode errorCode = ErrorCode.VALIDATION_FAILED;
        return ResponseEntity.badRequest()
            .body(new ErrorResponse(Instant.now(), errorCode.getCode(), errorCode.getMessage(),
                errors, e.getClass().getSimpleName(), errorCode.getHttpStatus().value()));

    }
}
