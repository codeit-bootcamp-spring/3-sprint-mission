package com.sprint.mission.discodeit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.util.Map;
import java.util.NoSuchElementException;

/*전체 API의 전역 예외를 처리하는 핸들러 클래스*/
@RestControllerAdvice
public class GlobalExceptionHandler {

    //잘못된 요청을 보냈을 때 예외 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(buildError("Bad Request", e.getMessage()));
    }

    //데이터가 존재하지 않을 때 예외 처리
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(NoSuchElementException e) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(buildError("Not Found", e.getMessage()));
    }

    //파일 업로드/다운로드 등의 I/O 처리 중 발생하는 예외 처리
    @ExceptionHandler(IOException.class)
    public ResponseEntity<Map<String, String>> handleIOException(IOException e) {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(buildError("File Error", e.getMessage()));
    }

    /**
     * 예외 타입을 특정하지 못한 예상 밖의 모든 오류
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleOtherRuntime(Exception e) {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(buildError("Internal Server Error", e.getMessage()));
    }


    private Map<String, String> buildError(String error, String message) {
        return Map.of("error", error, "message", message);
    }
}

