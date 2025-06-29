package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;

import lombok.Getter;

@Getter
public class DiscodeitException extends RuntimeException {

  private final Instant timestamp;
  private final ErrorCode errorCode;
  private final Map<String, Object> details; // 예외 상세 정보

  /**
   * 모든 필드를 포함한 생성자
   */
  public DiscodeitException(Instant timestamp, ErrorCode errorCode, Map<String, Object> details) {
    super(errorCode.getMessage());
    this.timestamp = timestamp;
    this.errorCode = errorCode;
    this.details = details != null ? details : Collections.emptyMap();
  }

  /**
   * ErrorCode와 details를 받는 생성자 (timestamp는 현재 시간으로 자동 설정)
   */
  public DiscodeitException(ErrorCode errorCode, Map<String, Object> details) {
    this(Instant.now(), errorCode, details);
  }

  /**
   * ErrorCode만 받는 생성자 (details는 빈 Map으로 설정)
   */
  public DiscodeitException(ErrorCode errorCode) {
    this(errorCode, Collections.emptyMap());
  }

  /**
   * ErrorCode와 cause를 받는 생성자
   */
  public DiscodeitException(ErrorCode errorCode, Throwable cause) {
    super(errorCode.getMessage(), cause);
    this.timestamp = Instant.now();
    this.errorCode = errorCode;
    this.details = Collections.emptyMap();
  }

  /**
   * ErrorCode, details, cause를 받는 생성자
   */
  public DiscodeitException(ErrorCode errorCode, Map<String, Object> details, Throwable cause) {
    super(errorCode.getMessage(), cause);
    this.timestamp = Instant.now();
    this.errorCode = errorCode;
    this.details = details != null ? details : Collections.emptyMap();
  }

  // === 정적 팩토리 메서드들 ===

  /**
   * ErrorCode만으로 예외를 생성하는 정적 팩토리 메서드
   */
  public static DiscodeitException of(ErrorCode errorCode) {
    return new DiscodeitException(errorCode);
  }

  /**
   * ErrorCode와 details로 예외를 생성하는 정적 팩토리 메서드
   */
  public static DiscodeitException of(ErrorCode errorCode, Map<String, Object> details) {
    return new DiscodeitException(errorCode, details);
  }

  /**
   * ErrorCode와 cause로 예외를 생성하는 정적 팩토리 메서드
   */
  public static DiscodeitException of(ErrorCode errorCode, Throwable cause) {
    return new DiscodeitException(errorCode, cause);
  }

  /**
   * ErrorCode, details, cause로 예외를 생성하는 정적 팩토리 메서드
   */
  public static DiscodeitException of(ErrorCode errorCode, Map<String, Object> details, Throwable cause) {
    return new DiscodeitException(errorCode, details, cause);
  }

  /**
   * 단일 키-값 쌍의 detail을 가진 예외를 생성하는 편의 메서드
   */
  public static DiscodeitException withDetail(ErrorCode errorCode, String key, Object value) {
    return new DiscodeitException(errorCode, Map.of(key, value));
  }

  /**
   * HTTP 상태 코드를 반환하는 편의 메서드
   */
  public int getStatusCode() {
    return errorCode.getStatusCode();
  }

  /**
   * 예외 정보를 문자열로 반환
   */
  @Override
  public String toString() {
    return String.format("DiscodeitException{timestamp=%s, errorCode=%s, message='%s', details=%s}",
        timestamp, errorCode, getMessage(), details);
  }
}
