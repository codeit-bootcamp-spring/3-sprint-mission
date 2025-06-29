package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.dto.response.ErrorResponse;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/*전체 API의 전역 예외를 처리하는 핸들러 클래스*/
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DiscodeitException.class)
    public ResponseEntity<ErrorResponse> handleDiscodeitException(DiscodeitException ex) {
        log.warn("[예외 처리] DiscodeitException 발생: {}", ex.getMessage(), ex);
        log.debug("[예외 처리] 에러 코드: {}, HTTP 상태: {}", ex.getErrorCode(),
            ex.getErrorCode().getStatus());

        return ResponseEntity.status(ex.getErrorCode().getStatus())
            .body(ErrorResponse.of(ex));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleOtherException(Exception ex) {
        log.error("[예외 처리] 예상치 못한 예외 발생: {}", ex.getMessage(), ex);

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse(
                Instant.now(),
                "UNEXPECTED_EXCEPTION",
                ex.getMessage() != null ? ex.getMessage() : "알 수 없는 오류입니다.",
                Map.of(),
                ex.getClass().getSimpleName(),
                HttpStatus.INTERNAL_SERVER_ERROR.value()
            ));
    }


    /**
     * @Valid 유효성 검사 실패 시 발생하는 예외 처리 (@RequestBody 사용 시)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
        MethodArgumentNotValidException ex) {

        log.error("유효성 검사 실패: {}", ex.getMessage());

        Map<String, Object> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(
                Instant.now(),
                "VALIDATION_FAILED",
                "입력 데이터 유효성 검사에 실패했습니다.",
                errors,
                ex.getClass().getSimpleName(),
                HttpStatus.BAD_REQUEST.value()
            ));
    }

    /**
     * @ModelAttribute 또는 폼 데이터 유효성 검사 실패 시 발생하는 예외 처리
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(BindException ex) {

        log.error("바인딩 유효성 검사 실패: {}", ex.getMessage());

        Map<String, Object> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(
                Instant.now(),
                "BINDING_VALIDATION_FAILED",
                "폼 데이터 유효성 검사에 실패했습니다",
                errors,
                ex.getClass().getSimpleName(),
                HttpStatus.BAD_REQUEST.value()
            ));
    }
}

