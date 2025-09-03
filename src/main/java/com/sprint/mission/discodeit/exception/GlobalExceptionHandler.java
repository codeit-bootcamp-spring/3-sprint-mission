package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.exception.auth.AuthException;
import com.sprint.mission.discodeit.exception.auth.InvalidRefreshTokenException;
import com.sprint.mission.discodeit.exception.auth.RefreshTokenNotFoundException;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentException;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateNotAllowedException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.notification.NotificationAccessDeniedException;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusAlreadyExistsException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserEmailAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNameAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import java.nio.file.AccessDeniedException;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  /** 유저 관련 Error
   * 1.User Not Found Exception
   * */
  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleException(UserNotFoundException e) {
    ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND, e.getErrorCode());
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(errorResponse);
  }

  /** 유저 관련 Error
   * 2.User Duplicate Exception
   * */
  @ExceptionHandler(UserEmailAlreadyExistsException.class)
  public ResponseEntity<ErrorResponse> handleException(UserEmailAlreadyExistsException e) {
    ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, e.getErrorCode());
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(errorResponse);
  }

  @ExceptionHandler(UserNameAlreadyExistsException.class)
  public ResponseEntity<ErrorResponse> handleException(UserNameAlreadyExistsException e){
    ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, e.getErrorCode());
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(errorResponse);
  }

  /** 채널 관련 Error
   * 3.Channel Not Found Exception
   * */
  @ExceptionHandler(ChannelNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleException(ChannelNotFoundException e) {
    ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND, e.getErrorCode());
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(errorResponse);
  }

  /** 채널 관련 Error
   * 4.Private Channel Update Exception
   * */
  @ExceptionHandler(PrivateChannelUpdateNotAllowedException.class)
  public ResponseEntity<ErrorResponse> handleException(PrivateChannelUpdateNotAllowedException e) {
    ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, e.getErrorCode());
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(errorResponse);
  }

  /** 메시지 관련 Error
   * 5.Message Not Found Exception
   * */
  @ExceptionHandler(MessageNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleException(MessageNotFoundException e) {
     ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND, e.getErrorCode());
     return ResponseEntity
         .status(HttpStatus.NOT_FOUND)
         .body(errorResponse);
   }

   /** 권한 관련 Error
    * 6.Auth Exception
    * */
  @ExceptionHandler(AuthException.class)
  public ResponseEntity<ErrorResponse> handleException(AuthException e) {
    ErrorResponse errorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED, e.getErrorCode());
    return ResponseEntity
        .status(HttpStatus.UNAUTHORIZED)
        .body(errorResponse);
  }

  /** 접근 거부 Error
   * 7. Access Denied Exception
   * */

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorResponse> handleException(AccessDeniedException e) {
    ErrorResponse errorResponse = new ErrorResponse(
        Instant.now(),
        HttpStatus.FORBIDDEN.getReasonPhrase(),
        "접근 권한이 없습니다.",
        e.getClass().getSimpleName(),
        HttpStatus.FORBIDDEN.value(),
        null
    );
    return ResponseEntity
        .status(HttpStatus.FORBIDDEN)
        .body(errorResponse);
  }

  /** 파일 관련 Error
   * 8. BinaryContent Exception
   * */
  @ExceptionHandler(BinaryContentException.class)
  public ResponseEntity<ErrorResponse> handleException(BinaryContentException e) {
    ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND, e.getErrorCode());
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(errorResponse);
  }

  /** 읽음 상태 관련 Error
   * 9.ReadStatus Not Found Exception
   * */
  @ExceptionHandler(ReadStatusNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleException(ReadStatusNotFoundException e) {
    ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND, e.getErrorCode());
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(errorResponse);
  }

  /** 읽음 상태 관련 Error
   * 10.ReadStatus Duplicate Exception
   * */
  @ExceptionHandler(ReadStatusAlreadyExistsException.class)
  public ResponseEntity<ErrorResponse> handleException(ReadStatusAlreadyExistsException e) {
    ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, e.getErrorCode());
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(errorResponse);
  }

  /**
   * 13. 유효성 검사 실패 시 발생 에러
   * */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleException(MethodArgumentNotValidException ex) {
    String error = ex.getBindingResult().getFieldErrors().stream()
        .findFirst()
        .map(err -> err.getDefaultMessage() + " " + err.getRejectedValue())
        .toString();

    ErrorResponse errorResponse = new ErrorResponse(
        Instant.now(),
        HttpStatus.BAD_REQUEST.getReasonPhrase(),
        error,
        ex.getClass().getSimpleName(),
        HttpStatus.BAD_REQUEST.value(),
        null);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(
            errorResponse
        );
  }

  /** 알림 관련 Error
   * 14.Notification Not Found Exception
   * */
  @ExceptionHandler(NotificationNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotificationNotFoundException(NotificationNotFoundException e) {
    ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND, e.getErrorCode());
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(errorResponse);
  }

  /** 알림 관련 Error
   * 15.Notification Access Denied Exception
   * */
  @ExceptionHandler(NotificationAccessDeniedException.class)
  public ResponseEntity<ErrorResponse> handleNotificationAccessDeniedException(NotificationAccessDeniedException e) {
    ErrorResponse errorResponse = new ErrorResponse(HttpStatus.FORBIDDEN, e.getErrorCode());
    return ResponseEntity
        .status(HttpStatus.FORBIDDEN)
        .body(errorResponse);
  }


  /** Refresh Token 관련 에러
   *  16. InvalidRefreshTokenException & RefreshTokenNotFoundException
   * */
  @ExceptionHandler({InvalidRefreshTokenException.class, RefreshTokenNotFoundException.class})
  public ResponseEntity<ErrorResponse> handleRefreshTokenException(DiscodeitException e) {
    ErrorResponse errorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED, e.getErrorCode());
    return ResponseEntity
        .status(HttpStatus.UNAUTHORIZED)
        .body(errorResponse);
  }

  /**
   *  17. 예상치 못한 에러 관련
   * */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleException(Exception e) {
    e.printStackTrace();
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(e.getMessage());
  }

}
