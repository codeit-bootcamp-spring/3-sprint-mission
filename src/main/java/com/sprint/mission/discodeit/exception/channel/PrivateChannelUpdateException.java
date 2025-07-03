package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

/**
 * 비공개 채널 수정 시도 시 발생하는 예외
 */
public class PrivateChannelUpdateException extends ChannelException {

  public PrivateChannelUpdateException() {
    super(ErrorCode.PRIVATE_CHANNEL_UPDATE);
  }

  public PrivateChannelUpdateException(Map<String, Object> details) {
    super(ErrorCode.PRIVATE_CHANNEL_UPDATE, details);
  }

  public PrivateChannelUpdateException(Throwable cause) {
    super(ErrorCode.PRIVATE_CHANNEL_UPDATE, cause);
  }

  public PrivateChannelUpdateException(Map<String, Object> details, Throwable cause) {
    super(ErrorCode.PRIVATE_CHANNEL_UPDATE, details, cause);
  }

  // === 편의 정적 팩토리 메서드들 ===

  /**
   * 채널 ID로 예외를 생성하는 정적 팩토리 메서드
   */
  public static PrivateChannelUpdateException withChannelId(UUID channelId) {
    return new PrivateChannelUpdateException(Map.of(
        "channelId", channelId,
        "channelType", "PRIVATE",
        "attemptedAction", "UPDATE"));
  }

  /**
   * 채널 ID와 사용자 ID로 예외를 생성하는 정적 팩토리 메서드
   */
  public static PrivateChannelUpdateException withChannelIdAndUserId(UUID channelId, UUID userId) {
    return new PrivateChannelUpdateException(Map.of(
        "channelId", channelId,
        "userId", userId,
        "channelType", "PRIVATE",
        "attemptedAction", "UPDATE"));
  }

  /**
   * 일반적인 생성 메서드
   */
  public static PrivateChannelUpdateException of() {
    return new PrivateChannelUpdateException();
  }

  /**
   * 상세 정보와 함께 생성하는 메서드
   */
  public static PrivateChannelUpdateException of(Map<String, Object> details) {
    return new PrivateChannelUpdateException(details);
  }
}