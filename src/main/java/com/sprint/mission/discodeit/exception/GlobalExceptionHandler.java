package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(DiscodeitException.class)
  public ResponseEntity<ErrorResponse> handleBusinessException(DiscodeitException e) {
    log.error("[서비스 오류] code: {}, exceptionType: {}", e.getErrorCode().name(),
        e.getClass().getSimpleName(), e);

    return ResponseEntity
        .status(e.getErrorCode().getStatus())
        .body(ErrorResponse.from(e));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGeneralException(Exception e) {

    // 크롬 버전 때문에 출력되는 에러
    if (e.getMessage()
        .equals("No static resource .well-known/appspecific/com.chrome.devtools.json.")) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    log.error("[시스템 오류] {}", e.getMessage(), e);

    ErrorResponse response = new ErrorResponse(
        "INTERNAL_SERVER_ERROR",
        "알 수 없는 서버 에러가 발생했습니다.",
        e.getClass().getSimpleName(),
        HttpStatus.INTERNAL_SERVER_ERROR.value(),
        Instant.now(),
        Map.of("cause", e.getMessage())
    );

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
  }
}
