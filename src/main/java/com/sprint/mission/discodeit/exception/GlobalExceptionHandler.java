package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.dto.response.ErrorResponse;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {

    log.error("[서비스 오류] code: {}, messageKey: {}", e.getErrorCode().getMessage(),
        e.getErrorCode().getMessageKey(), e);

    ErrorCode errorCode = e.getErrorCode();

    ErrorResponse response = new ErrorResponse(
        errorCode.getMessage(),
        errorCode.getMessageKey(),
        errorCode.getStatus(),
        Instant.now()
    );

    return ResponseEntity.status(errorCode.getStatus()).body(response);
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
        HttpStatus.INTERNAL_SERVER_ERROR.value(),
        Instant.now()
    );
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
  }
}
