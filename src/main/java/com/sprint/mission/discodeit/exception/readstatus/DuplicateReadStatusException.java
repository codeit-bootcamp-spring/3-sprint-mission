package com.sprint.mission.discodeit.exception.readstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

/**
 * 중복된 읽기 상태 생성 시도 시 발생하는 예외
 */
public class DuplicateReadStatusException extends ReadStatusException {

  public DuplicateReadStatusException() {
    super(ErrorCode.DUPLICATE_READ_STATUS);
  }

  public DuplicateReadStatusException(Map<String, Object> details) {
    super(ErrorCode.DUPLICATE_READ_STATUS, details);
  }

  public DuplicateReadStatusException(Throwable cause) {
    super(ErrorCode.DUPLICATE_READ_STATUS, cause);
  }

  public DuplicateReadStatusException(Map<String, Object> details, Throwable cause) {
    super(ErrorCode.DUPLICATE_READ_STATUS, details, cause);
  }

  // === 편의 정적 팩토리 메서드들 ===

  /**
   * 사용자 ID와 메시지 ID로 예외를 생성하는 정적 팩토리 메서드
   */
  public static DuplicateReadStatusException withUserIdAndMessageId(UUID userId, UUID messageId) {
    return new DuplicateReadStatusException(Map.of(
        "userId", userId,
        "messageId", messageId,
        "duplicateField", "userId_messageId"));
  }

  /**
   * 사용자 ID와 채널 ID로 예외를 생성하는 정적 팩토리 메서드
   */
  public static DuplicateReadStatusException withUserIdAndChannelId(UUID userId, UUID channelId) {
    return new DuplicateReadStatusException(Map.of(
        "userId", userId,
        "channelId", channelId,
        "duplicateField", "userId_channelId"));
  }

  /**
   * 일반적인 생성 메서드
   */
  public static DuplicateReadStatusException of() {
    return new DuplicateReadStatusException();
  }

  /**
   * 상세 정보와 함께 생성하는 메서드
   */
  public static DuplicateReadStatusException of(Map<String, Object> details) {
    return new DuplicateReadStatusException(details);
  }
}