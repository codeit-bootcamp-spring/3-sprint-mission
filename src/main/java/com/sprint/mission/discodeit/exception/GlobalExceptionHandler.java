package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.dto.response.ErrorResponse;
import com.sprint.mission.discodeit.exception.Channel.ChannelException;
import com.sprint.mission.discodeit.exception.Common.CommonException;
import com.sprint.mission.discodeit.exception.Message.MessageException;
import com.sprint.mission.discodeit.exception.ReadStatus.ReadStatusException;
import com.sprint.mission.discodeit.exception.User.UserException;
import com.sprint.mission.discodeit.exception.UserStatus.UserStatusException;
import java.time.Instant;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends RuntimeException {

  @ExceptionHandler(UserException.class)
  public ResponseEntity<ErrorResponse> handleUserException(UserException e) {

    // 도메인 별 공통 로깅
    log.error("▶▶▶▶Exception(사용자) 발생 : {} - {}", e.getErrorCode().getCode(), e.getMessage());

    // 컨텍스트 정보 로깅
    e.getDetails().forEach((key, value) ->
        log.debug("▶▶▶▶▶Exception(사용자) 세부정보 : {} = {}", key, value));

    return ResponseEntity.status(e.getErrorCode().getHttpStatus())
        .body(
            new ErrorResponse(
                e.getTimestamp(), e.getErrorCode().getCode(),
                e.getMessage(), e.getDetails(),
                e.getClass().getTypeName(),
                e.getErrorCode().getHttpStatus()
            )
        );
  }

  @ExceptionHandler(UserStatusException.class)
  public ResponseEntity<ErrorResponse> handleException(UserStatusException e) {
    // 도메인 별 공통 로깅
    log.error("▶▶▶▶Exception(사용자상태) 발생 : {} - {}", e.getErrorCode().getCode(), e.getMessage());

    // 컨텍스트 정보 로깅
    e.getDetails().forEach((key, value) ->
        log.debug("▶▶▶▶▶Exception(사용자상태) 세부정보 : {} = {}", key, value));

    return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(
        new ErrorResponse(
            e.getTimestamp(), e.getErrorCode().getCode(),
            e.getMessage(), e.getDetails(),
            e.getClass().getTypeName(),
            e.getErrorCode().getHttpStatus()
        )
    );
  }

  @ExceptionHandler(ChannelException.class)
  public ResponseEntity<ErrorResponse> handleChannelException(ChannelException e) {
    // 도메인 별 공통 로깅
    log.error("▶▶▶▶Exception(채널) 발생 : {} - {}", e.getErrorCode().getCode(), e.getMessage());

    // 컨텍스트 정보 로깅
    e.getDetails().forEach((key, value) ->
        log.debug("▶▶▶▶▶Exception(채널) 세부정보 : {} = {}", key, value));

    return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(
        new ErrorResponse(
            e.getTimestamp(), e.getErrorCode().getCode(),
            e.getMessage(), e.getDetails(),
            e.getClass().getTypeName(),
            e.getErrorCode().getHttpStatus()
        )
    );
  }

  @ExceptionHandler(ReadStatusException.class)
  public ResponseEntity<ErrorResponse> handleReadStatusException(ReadStatusException e) {
    // 도메인 별 공통 로깅
    log.error("▶▶▶▶Exception(읽기상태) 발생 : {} - {}", e.getErrorCode().getCode(), e.getMessage());

    // 컨텍스트 정보 로깅
    e.getDetails().forEach((key, value) ->
        log.debug("▶▶▶▶▶Exception(읽기상태) 세부정보 : {} = {}", key, value));

    return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(
        new ErrorResponse(
            e.getTimestamp(), e.getErrorCode().getCode(),
            e.getMessage(), e.getDetails(),
            e.getClass().getTypeName(),
            e.getErrorCode().getHttpStatus()
        )
    );
  }

  @ExceptionHandler(MessageException.class)
  public ResponseEntity<ErrorResponse> handleMessageException(MessageException e) {
    // 도메인 별 공통 로깅
    log.error("▶▶▶▶Exception(메세지) 발생 : {} - {}", e.getErrorCode().getCode(), e.getMessage());

    // 컨텍스트 정보 로깅
    e.getDetails().forEach((key, value) ->
        log.debug("▶▶▶▶▶Exception(메세지) 세부정보 : {} = {}", key, value));

    return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(
        new ErrorResponse(
            e.getTimestamp(), e.getErrorCode().getCode(),
            e.getMessage(), e.getDetails(),
            e.getClass().getTypeName(),
            e.getErrorCode().getHttpStatus()
        )
    );
  }

  @ExceptionHandler(CommonException.class)
  public ResponseEntity<ErrorResponse> handleCommonException(CommonException e) {
    // 도메인 별 공통 로깅
    log.error("▶▶▶▶Exception(공통) 발생 : {} - {}", e.getErrorCode().getCode(), e.getMessage());

    // 컨텍스트 정보 로깅
    e.getDetails().forEach((key, value) ->
        log.debug("▶▶▶▶▶Exception(공통) 세부정보 : {} = {}", key, value));

    return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(
        new ErrorResponse(
            e.getTimestamp(), e.getErrorCode().getCode(),
            e.getMessage(), e.getDetails(),
            e.getClass().getTypeName(),
            e.getErrorCode().getHttpStatus()
        )
    );
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidException(MethodArgumentNotValidException e) {
    // 도메인 별 공통 로깅
    log.error("▶▶▶▶request 데이터 검증 실패 : {} - {}", e.getMessage(), e.getParameter());

    return ResponseEntity.status(ErrorCode.INVALID_REQUEST_PARAMS.getHttpStatus()).body(
        new ErrorResponse(
            Instant.now(), ErrorCode.INVALID_REQUEST_PARAMS.getCode(),
            e.getMessage(), null,
            e.getClass().getTypeName(),
            ErrorCode.INVALID_REQUEST_PARAMS.getHttpStatus()
        )
    );
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<?> handleRuntime(RuntimeException ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(Map.of("error", ex.getMessage()));
  }

}
