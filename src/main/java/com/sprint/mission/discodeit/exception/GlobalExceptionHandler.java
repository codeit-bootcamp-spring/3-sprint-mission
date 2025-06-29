package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = Logger.getLogger(GlobalExceptionHandler.class.getName());


    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("잘못된 요청입니다.: " + ex.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseBody
    public ResponseEntity<String> handleNoSuchElement(NoSuchElementException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("리소스를 찾을 수 없습니다.: " + ex.getMessage());
    }

    @ExceptionHandler(NullPointerException.class)
    @ResponseBody
    public ResponseEntity<String> handleNullPointer(NullPointerException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("널포인터 예외가 발생했습니다.: " + ex.getMessage());
    }

    @ExceptionHandler(DiscodeitException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleDiscodeitExcetion(DiscodeitException e){
        log.warn("[GlobalExceptionHandler] DiscodeitException 발생: {}", e.getMessage(), e);

        int status = e.getErrorCode().getStatus();
        ErrorResponse errorResponse = ErrorResponse.of(e);

        return ResponseEntity
                .status(status)
                .body(errorResponse);
    }

    // Valid 검증 실패 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException e){
        log.warn("[GlobalExceptionHandler] MethodArgumentNotValidException 발생: {}", e.getMessage(), e);

        Map<String, Object> details = new HashMap<>();

        // 필드별 오류 메시지 수집
        Map<String, Object> fieldErrors = e.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> error.getDefaultMessage() != null ?
                                error.getDefaultMessage() : "유효성 검증 실패",  // null일 때 기본값
                        (existing, replacement) -> existing
                ));

        // 글로벌 오류 메시지 수집
        List<String> globalErrors = e.getBindingResult().getGlobalErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();

        details.put("fieldErrors", fieldErrors);

        if (!globalErrors.isEmpty()) {
            details.put("globalErrors", globalErrors);
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(Instant.now())
                .code("VALIDATION_FAILED")
                .message("입력값 검증에 실패했습니다.")
                .details(details)
                .exceptionType("MethodArgumentNotValidException")
                .status(400)
                .build();

        return ResponseEntity
                .badRequest()
                .body(errorResponse);
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handlerValidationException(ValidationException e){
        log.warn("[GlobalExceptionHandler] ValidationException 발생: {}", e.getMessage());

        int status = e.getErrorCode().getStatus();

        ErrorResponse errorResponse = ErrorResponse.of(e);

        return ResponseEntity
                .status(status)
                .body(errorResponse);
    }


    // defalt 예외 처리기
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<String> handleException(Exception ex) {
        log.warn("[GlobalExceptionHandler] 서버 내부 오류 발생", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("서버 내부 오류: " + ex.getMessage());
    }
}
