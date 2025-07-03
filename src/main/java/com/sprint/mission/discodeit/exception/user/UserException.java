package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

import java.time.Instant;
import java.util.Map;

/**
 * 사용자 도메인 관련 예외의 기본 클래스
 * 사용자와 관련된 모든 예외는 이 클래스를 상속받습니다.
 */
public abstract class UserException extends DiscodeitException {

  /**
   * ErrorCode만 받는 생성자
   */
  protected UserException(ErrorCode errorCode) {
    super(errorCode);
  }

  /**
   * ErrorCode와 details를 받는 생성자
   */
  protected UserException(ErrorCode errorCode, Map<String, Object> details) {
    super(errorCode, details);
  }

  /**
   * ErrorCode와 cause를 받는 생성자
   */
  protected UserException(ErrorCode errorCode, Throwable cause) {
    super(errorCode, cause);
  }

  /**
   * ErrorCode, details, cause를 받는 생성자
   */
  protected UserException(ErrorCode errorCode, Map<String, Object> details, Throwable cause) {
    super(errorCode, details, cause);
  }

  /**
   * 모든 필드를 받는 생성자
   */
  protected UserException(Instant timestamp, ErrorCode errorCode, Map<String, Object> details) {
    super(timestamp, errorCode, details);
  }
}