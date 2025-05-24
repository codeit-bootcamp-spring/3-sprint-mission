package com.sprint.mission.discodeit.controller.exception;

import com.sprint.mission.discodeit.controller.errorcode.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.NoSuchElementException;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice // 전역 예외 처리 클래스
public class GlobalExceptionHandler extends RuntimeException /* 비검사 예외 */ {

    @ExceptionHandler(InvalidArgException.class)
    protected ResponseEntity<ErrorDto> handleInvalidArgException(InvalidArgException e) {
        return ErrorDto.toResponseEntity(e.getErrorCode());
    }

    @ExceptionHandler(MatchNotFoundException.class)
    protected ResponseEntity<ErrorDto> handleMatchNotFoundException(MatchNotFoundException e) {
        return ErrorDto.toResponseEntity(e.getErrorCode());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleException(NoSuchElementException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // 404
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); // 400
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage()); // 500
    }
}
