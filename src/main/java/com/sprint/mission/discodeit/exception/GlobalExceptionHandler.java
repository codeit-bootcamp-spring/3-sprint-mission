package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.dto.response.ErrorResponse;
import java.time.Instant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LogManager.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {

    log.error("[서비스 오류] code: {}, messageKey: {}", e.getErrorCode().getCode(),
        e.getErrorCode().getMessageKey(), e);

    ErrorCode errorCode = e.getErrorCode();

    HttpStatus status = mapToHttpStatus(errorCode);

    ErrorResponse response = new ErrorResponse(
        errorCode.getCode(),
        errorCode.getMessageKey(),
        status.value(),
        Instant.now()
    );

    return ResponseEntity.status(status).body(response);
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

  private HttpStatus mapToHttpStatus(ErrorCode errorCode) {
    return switch (errorCode) {
      case NOT_FOUND -> HttpStatus.NOT_FOUND;
      case INVALID_INPUT -> HttpStatus.BAD_REQUEST;
      case UNAUTHORIZED -> HttpStatus.UNAUTHORIZED;
      case FORBIDDEN -> HttpStatus.FORBIDDEN;
      case ALREADY_EXISTS -> HttpStatus.CONFLICT;
      case PROCESSING_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
      default -> HttpStatus.INTERNAL_SERVER_ERROR;
    };
  }
}
