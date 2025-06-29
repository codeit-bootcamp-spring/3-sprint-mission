package com.sprint.mission.discodeit.exception.readstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

/**
 * 읽기 상태를 찾을 수 없을 때 발생하는 예외
 */
public class ReadStatusNotFoundException extends ReadStatusException {

  public ReadStatusNotFoundException() {
    super(ErrorCode.READ_STATUS_NOT_FOUND);
  }

  public ReadStatusNotFoundException(Map<String, Object> details) {
    super(ErrorCode.READ_STATUS_NOT_FOUND, details);
  }

  public ReadStatusNotFoundException(Throwable cause) {
    super(ErrorCode.READ_STATUS_NOT_FOUND, cause);
  }

  public ReadStatusNotFoundException(Map<String, Object> details, Throwable cause) {
    super(ErrorCode.READ_STATUS_NOT_FOUND, details, cause);
  }

  // === 편의 정적 팩토리 메서드들 ===

  /**
   * 읽기 상태 ID로 예외를 생성하는 정적 팩토리 메서드
   */
  public static ReadStatusNotFoundException withReadStatusId(UUID readStatusId) {
    return new ReadStatusNotFoundException(Map.of("readStatusId", readStatusId));
  }

  /**
   * 사용자 ID와 메시지 ID로 예외를 생성하는 정적 팩토리 메서드
   */
  public static ReadStatusNotFoundException withUserIdAndMessageId(UUID userId, UUID messageId) {
    return new ReadStatusNotFoundException(Map.of(
        "userId", userId,
        "messageId", messageId));
  }

  /**
   * 사용자 ID와 채널 ID로 예외를 생성하는 정적 팩토리 메서드
   */
  public static ReadStatusNotFoundException withUserIdAndChannelId(UUID userId, UUID channelId) {
    return new ReadStatusNotFoundException(Map.of(
        "userId", userId,
        "channelId", channelId));
  }

  /**
   * 일반적인 생성 메서드
   */
  public static ReadStatusNotFoundException of() {
    return new ReadStatusNotFoundException();
  }

  /**
   * 상세 정보와 함께 생성하는 메서드
   */
  public static ReadStatusNotFoundException of(Map<String, Object> details) {
    return new ReadStatusNotFoundException(details);
  }
}