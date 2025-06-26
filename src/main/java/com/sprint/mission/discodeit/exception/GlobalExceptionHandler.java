package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 커스텀 예외 처리
    @ExceptionHandler(DiscodeitException.class)
    public ResponseEntity<ErrorResponse> handleDiscodeitException(
        DiscodeitException e
    ) {
        HttpStatus status = mapToHttpStatus(e.getErrorCode());
        ErrorResponse response = ErrorResponse.from(e, status.value());
        return ResponseEntity.status(status).body(response);
    }

    // 그 외 런타임 예외
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(
        RuntimeException e,
        HttpServletRequest request
    ) {
        ErrorResponse response = new ErrorResponse(
            Instant.now(),
            "UNEXPECTED_ERROR",
            e.getMessage(),
            Map.of("requestUri", request.getRequestURI()),
            e.getClass().getSimpleName(),
            HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    private HttpStatus mapToHttpStatus(ErrorCode code) {
        return switch (code) {
            case USER_NOT_FOUND, CHANNEL_NOT_FOUND, MESSAGE_NOT_FOUND,
                 READSTATUS_NOT_FOUND, USERSTATUS_NOT_FOUND, BINARY_CONTENT_NOT_FOUND,
                 AUTHOR_NOT_FOUND -> HttpStatus.NOT_FOUND;

            case DUPLICATE_USER_NAME, DUPLICATE_USER_EMAIL,
                 DUPLICATE_READSTATUS, DUPLICATE_USERSTATUS -> HttpStatus.CONFLICT;

            case PRIVATE_CHANNEL_UPDATE, WRONG_PASSWORD -> HttpStatus.BAD_REQUEST;

            default -> HttpStatus.BAD_REQUEST;
        };
    }

    // @Valid 검증 실패 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
        MethodArgumentNotValidException e) {
        Map<String, Object> details = e.getBindingResult()
            .getFieldErrors() // @Vaild에 실패한 필드 리스트 가져옴
            .stream()
            .collect(
                Collectors.toMap(
                    FieldError::getField,
                    DefaultMessageSourceResolvable::getDefaultMessage,
                    (msg1, msg2) -> msg1  // 필드 중복되면 첫 번째 메시지 유지
                )
            );

        ErrorResponse response = new ErrorResponse(
            Instant.now(),
            "INVALID_REQUEST",
            "요청 데이터가 유효하지 않습니다.",
            details,
            e.getClass().getSimpleName(),
            HttpStatus.BAD_REQUEST.value()
        );
        return ResponseEntity.badRequest().body(response);
    }
}