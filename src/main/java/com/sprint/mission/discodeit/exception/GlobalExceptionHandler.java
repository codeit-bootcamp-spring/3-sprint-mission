package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.dto.response.ErrorResponse;
import java.time.Instant;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
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

    // 커스텀 예외 처리 전용 핸들러
    @ExceptionHandler(DiscodeitException.class)
    public ResponseEntity<ErrorResponse> handleDiscodeitException(DiscodeitException e) {
        String exceptionName = e.getClass().getSimpleName();

        HttpStatus status = switch (exceptionName) {
            case "UserNotFoundException", "ChannelNotFoundException", "MessageNotFoundException",
                 "BinaryContentNotFoundException", "ReadStatusNotFoundException",
                 "UserStatusNotFoundException" -> HttpStatus.NOT_FOUND;

            case "EmailAlreadyExistsException", "UserNameAlreadyExistsException",
                 "ReadStatusAlreadyExistsException", "PrivateChannelModificationException",
                 "InvalidUsernameOrPasswordException", "UserStatusAlreadyExistsException",
                 "InvalidUserUpdateInputException", "InvalidMessageContentException" ->
                HttpStatus.BAD_REQUEST;

            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };

        ErrorResponse response = ErrorResponse.of(
            e.getTimestamp(),
            e.getErrorCode().name(),
            e.getErrorCode().getMessage(),
            e.getDetails(),
            exceptionName,
            status
        );

        return ResponseEntity.status(status).body(response);
    }

    // 잘못된 요청을 하는 경우
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(RuntimeException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ErrorResponse response = ErrorResponse.of(
            Instant.now(),
            status.name(),
            e.getMessage(),
            null,
            e.getClass().getSimpleName(),
            status
        );

        return ResponseEntity.status(status).body(response);
    }

    // 데이터가 존재하지 않을 경우
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NoSuchElementException e) {
        HttpStatus status = HttpStatus.NOT_FOUND;

        ErrorResponse response = ErrorResponse.of(
            Instant.now(),
            status.name(),
            e.getMessage(),
            null,
            e.getClass().getSimpleName(),
            status
        );

        return ResponseEntity.status(status).body(response);
    }

    // 실행 도중에 문제가 생겼을 경우
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        ErrorResponse response = ErrorResponse.of(
            Instant.now(),
            status.name(),
            e.getMessage(),
            null,
            e.getClass().getSimpleName(),
            status
        );

        return ResponseEntity.status(status).body(response);
    }

    // 나머지 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        ErrorResponse response = ErrorResponse.of(
            Instant.now(),
            status.name(),
            "예기치 않은 오류가 발생했습니다.",
            null,
            e.getClass().getSimpleName(),
            status
        );

        return ResponseEntity.status(status).body(response);
    }

    //유효성 검증 오류 시 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
        MethodArgumentNotValidException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        log.warn("[Validation] 유효성 검사 실패 - {}", e.getMessage());

        String message = e.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));

        Map<String, Object> details = e.getBindingResult().getFieldErrors().stream()
            .collect(Collectors.toMap(
                FieldError::getField,
                error -> Optional.ofNullable(error.getDefaultMessage())
                    .orElse("유효성 메세지가 존재하지 않음"),
                (m1, m2) -> m1 + ", " + m2
            ));

        ErrorResponse response = ErrorResponse.of(
            Instant.now(),
            "VALIDATION_ERROR",
            message,
            details,
            e.getClass().getSimpleName(),
            status
        );

        return ResponseEntity.status(status).body(response);
    }
}