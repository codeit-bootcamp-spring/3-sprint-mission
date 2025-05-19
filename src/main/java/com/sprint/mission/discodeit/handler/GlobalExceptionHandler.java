package com.sprint.mission.discodeit.handler;


import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.validation.BindException;


import java.util.NoSuchElementException;

// Web API의 전역적 예외 처리 담당
@RestController
public class GlobalExceptionHandler {

  // NullPointerException 처리
  @ExceptionHandler(NullPointerException.class)
  public ResponseEntity<String> handleNullPointerException(NullPointerException ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body("서버 내부 오류가 발생했습니다. (NullPointerException)");
  }

  // IllegalArgumentException 처리
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body("잘못된 요청입니다 : " + ex.getMessage());
  }


  // 상태코드 반응
  // Bad Request( 400 ) : 잘못된 요청 파라미터
  @ExceptionHandler({
      MethodArgumentTypeMismatchException.class,
      BindException.class,
      MissingServletRequestParameterException.class
  })
  public ResponseEntity<String> handleBadRequest(Exception ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body("요청이 올바르지 않습니다 : " + ex.getMessage());
  }

  // Not Found( 404 ) : 존재하지 않는 리소스
  @ExceptionHandler({
      ConfigDataResourceNotFoundException.class,
      NoSuchElementException.class
  })
  public ResponseEntity<String> handleNotFound(Exception ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body("리소스를 찾을 수 없습니다 : " + ex.getMessage());
  }

  // Unsupported Media Type( 415 ) : 지원하지 않는 Content-Type
  @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
  public ResponseEntity<String> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException ex) {
    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
        .body("지원하지 않는 콘텐츠 타입입니다. : " + ex.getMessage());
  }


  // Internal Server Error( 500 ) : 원인 불명의 서버 에러
  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleGeneric(Exception ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body("서버 내부 오류가 발생했습니다 : " + ex.getMessage());
  }
}
