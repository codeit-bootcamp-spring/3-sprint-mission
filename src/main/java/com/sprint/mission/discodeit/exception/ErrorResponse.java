package com.sprint.mission.discodeit.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

/**
 * 일관된 예외 응답을 위한 ErrorResponse 클래스
 * 모든 예외 응답은 이 클래스를 통해 반환됩니다.
 * 
 * <p>
 * 이 클래스는 다음 필드들을 포함합니다:
 * </p>
 * <ul>
 * <li><strong>timestamp</strong>: 예외 발생 시간 (ISO-8601 형식)</li>
 * <li><strong>code</strong>: ErrorCode enum의 이름 (예: "USER_NOT_FOUND",
 * "DUPLICATE_USER")</li>
 * <li><strong>message</strong>: 사용자에게 표시될 에러 메시지 (한국어)</li>
 * <li><strong>details</strong>: 예외 상세 정보 (선택적, 디버깅용)</li>
 * <li><strong>exceptionType</strong>: 발생한 예외의 클래스 이름 (예:
 * "UserNotFoundException")</li>
 * <li><strong>status</strong>: HTTP 상태 코드 (예: 404, 409, 500)</li>
 * </ul>
 * 
 * <p>
 * 사용 예시:
 * </p>
 * 
 * <pre>{@code
 * // ErrorCode로부터 생성
 * ErrorResponse response = ErrorResponse.of(ErrorCode.USER_NOT_FOUND);
 * 
 * // DiscodeitException으로부터 생성
 * ErrorResponse response = ErrorResponse.of(userNotFoundException);
 * 
 * // 일반 Exception을 ErrorCode로 감싸서 생성
 * ErrorResponse response = ErrorResponse.of(sqlException, ErrorCode.DATABASE_ERROR);
 * }</pre>
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

  /**
   * 예외 발생 시간
   */
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
  private Instant timestamp;

  /**
   * ErrorCode enum의 이름 (비즈니스 로직 관점의 에러 코드)
   */
  private String code;

  /**
   * 에러 메시지
   */
  private String message;

  /**
   * 예외 상세 정보 (선택적)
   */
  private Map<String, Object> details;

  /**
   * 발생한 예외의 클래스 이름 (기술적 관점)
   */
  private String exceptionType;

  /**
   * HTTP 상태 코드
   */
  private int status;

  /**
   * ErrorCode를 기반으로 ErrorResponse를 생성하는 정적 팩토리 메서드
   */
  public static ErrorResponse of(ErrorCode errorCode) {
    return new ErrorResponse(
        Instant.now(),
        errorCode.name(),
        errorCode.getMessage(),
        null,
        errorCode.name(),
        errorCode.getStatusCode());
  }

  /**
   * ErrorCode와 상세 정보를 기반으로 ErrorResponse를 생성하는 정적 팩토리 메서드
   */
  public static ErrorResponse of(ErrorCode errorCode, Map<String, Object> details) {
    return new ErrorResponse(
        Instant.now(),
        errorCode.name(),
        errorCode.getMessage(),
        details,
        errorCode.name(),
        errorCode.getStatusCode());
  }

  /**
   * DiscodeitException을 기반으로 ErrorResponse를 생성하는 정적 팩토리 메서드
   */
  public static ErrorResponse of(DiscodeitException exception) {
    return new ErrorResponse(
        exception.getTimestamp(),
        exception.getErrorCode().name(),
        exception.getMessage(),
        exception.getDetails(),
        exception.getClass().getSimpleName(),
        exception.getStatusCode());
  }

  /**
   * 일반 Exception을 기반으로 ErrorResponse를 생성하는 정적 팩토리 메서드
   */
  public static ErrorResponse of(Exception exception, ErrorCode errorCode) {
    return new ErrorResponse(
        Instant.now(),
        errorCode.name(),
        errorCode.getMessage(),
        Map.of("originalMessage", exception.getMessage()),
        exception.getClass().getSimpleName(),
        errorCode.getStatusCode());
  }

  /**
   * 커스텀 에러 응답을 생성하는 정적 팩토리 메서드
   */
  public static ErrorResponse of(int status, String exceptionType, String message) {
    return new ErrorResponse(
        Instant.now(),
        "CUSTOM_ERROR",
        message,
        null,
        exceptionType,
        status);
  }

  /**
   * 커스텀 에러 응답을 생성하는 정적 팩토리 메서드 (상세 정보 포함)
   */
  public static ErrorResponse of(int status, String exceptionType, String message, Map<String, Object> details) {
    return new ErrorResponse(
        Instant.now(),
        "CUSTOM_ERROR",
        message,
        details,
        exceptionType,
        status);
  }
}