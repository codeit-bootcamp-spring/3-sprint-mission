package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.exception.auth.AuthException;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentException;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateNotAllowedException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusAlreadyExistsException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserEmailAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNameAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusAlreadyExistsException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusNotFoundException;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.View;

@RestControllerAdvice
public class GlobalExceptionHandler {

  /* 유저 관련 Error
   * 1.User Not Found Exception */
  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleException(UserNotFoundException e) {
    ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND, e.getErrorCode());
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(errorResponse);
  }

  /* 유저 관련 Error
   * 2.User Duplicate Exception */
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

  /* 채널 관련 Error
   * 3.Channel Not Found Exception */
  @ExceptionHandler(ChannelNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleException(ChannelNotFoundException e) {
    ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND, e.getErrorCode());
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(errorResponse);
  }

  /* 채널 관련 Error
   * 4.Private Channel Update Exception */
  @ExceptionHandler(PrivateChannelUpdateNotAllowedException.class)
  public ResponseEntity<ErrorResponse> handleException(PrivateChannelUpdateNotAllowedException e) {
    ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, e.getErrorCode());
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(errorResponse);
  }

  /* 메시지 관련 Error
   * 5.Message Not Found Exception */
  @ExceptionHandler(MessageNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleException(MessageNotFoundException e) {
     ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND, e.getErrorCode());
     return ResponseEntity
         .status(HttpStatus.NOT_FOUND)
         .body(errorResponse);
   }

   /* 권한 관련 Error
    * 6.Auth Exception */
  @ExceptionHandler(AuthException.class)
  public ResponseEntity<ErrorResponse> handleException(AuthException e) {
    ErrorResponse errorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED, e.getErrorCode());
    return ResponseEntity
        .status(HttpStatus.UNAUTHORIZED)
        .body(errorResponse);
  }

  /* 파일 관련 Error
   * 7. BinaryContent Exception */
  @ExceptionHandler(BinaryContentException.class)
  public ResponseEntity<ErrorResponse> handleException(BinaryContentException e) {
    ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND, e.getErrorCode());
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(errorResponse);
  }

  /* 읽음 상태 관련 Error
   * 8.ReadStatus Not Found Exception */
  @ExceptionHandler(ReadStatusNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleException(ReadStatusNotFoundException e) {
    ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND, e.getErrorCode());
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(errorResponse);
  }

  /* 읽음 상태 관련 Error
   * 9.ReadStatus Duplicate Exception */
  @ExceptionHandler(ReadStatusAlreadyExistsException.class)
  public ResponseEntity<ErrorResponse> handleException(ReadStatusAlreadyExistsException e) {
    ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, e.getErrorCode());
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(errorResponse);
  }

  /* 유저 상태 관련 Error
   * 10. UserStatus Not Found Exception */
  @ExceptionHandler(UserStatusNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleException(UserStatusNotFoundException e) {
    ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND, e.getErrorCode());
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(errorResponse);
  }

  /* 유저 상태 관련 Error
   * 11. UserStatus Duplicate Exception */
  @ExceptionHandler(UserStatusAlreadyExistsException.class)
  public ResponseEntity<ErrorResponse> handleException(UserStatusAlreadyExistsException e) {
    ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, e.getErrorCode());
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(errorResponse);
  }

  /* 12. 유효성 검사 실패 시 발생 에러 */
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

  /* 13. 예상치 못한 에러 관련 */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleException(Exception e) {
    e.printStackTrace();
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(e.getMessage());
  }

}
