package com.sprint.mission.discodeit.handler;


import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.validation.BindException;


import java.util.NoSuchElementException;

// Web API의 전역적 예외 처리 담당
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 공통 응답 생성 메서드
    private ResponseEntity<String> buildResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(message);
    }

    // NullPointerException 처리
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<String> handleNullPointerException(NullPointerException ex) {
        ex.printStackTrace();
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,
            "서버 내부 오류가 발생했습니다.");
    }

    // IllegalArgumentException 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        ex.printStackTrace();
        return buildResponse(HttpStatus.BAD_REQUEST,
            "잘못된 요청입니다.");
    }


    // 상태코드 반응
    // Bad Request( 400 ) : 잘못된 요청 파라미터
    @ExceptionHandler({
        MethodArgumentTypeMismatchException.class,
        BindException.class,
        MissingServletRequestParameterException.class
    })
    public ResponseEntity<String> handleBadRequest(Exception ex) {
        ex.printStackTrace();
        return buildResponse(HttpStatus.BAD_REQUEST,
            "요청이 올바르지 않습니다");
    }

    // Not Found( 404 ) : 존재하지 않는 리소스
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNotFound(Exception ex) {
        ex.printStackTrace();
        return buildResponse(HttpStatus.NOT_FOUND,
            "리소스를 찾을 수 없습니다");
    }

    // Unsupported Media Type( 415 ) : 지원하지 않는 Content-Type
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<String> handleUnsupportedMediaType(
        HttpMediaTypeNotSupportedException ex) {
        ex.printStackTrace();
        return buildResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
            "지원하지 않는 콘텐츠 타입입니다");
    }


    // Internal Server Error( 500 ) : 원인 불명의 서버 에러
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneric(Exception ex) {
        ex.printStackTrace();
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,
            "서버 내부 오류가 발생했습니다");
    }


    // 별도 구분
    @ExceptionHandler(ConfigDataResourceNotFoundException.class)
    public ResponseEntity<String> handleNotFound(ConfigDataResourceNotFoundException ex) {
        ex.printStackTrace();
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,
            "서버 설정 오류가 발생했습니다");
    }
}
