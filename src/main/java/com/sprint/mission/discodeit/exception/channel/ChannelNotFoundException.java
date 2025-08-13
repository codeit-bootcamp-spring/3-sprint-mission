package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

/**
 * 채널을 찾을 수 없을 때 발생하는 예외
 */
public class ChannelNotFoundException extends ChannelException {

  public ChannelNotFoundException() {
    super(ErrorCode.CHANNEL_NOT_FOUND);
  }

  public ChannelNotFoundException(Map<String, Object> details) {
    super(ErrorCode.CHANNEL_NOT_FOUND, details);
  }

  public ChannelNotFoundException(Throwable cause) {
    super(ErrorCode.CHANNEL_NOT_FOUND, cause);
  }

  public ChannelNotFoundException(Map<String, Object> details, Throwable cause) {
    super(ErrorCode.CHANNEL_NOT_FOUND, details, cause);
  }

  // === 편의 정적 팩토리 메서드들 ===

  /**
   * 채널 ID로 예외를 생성하는 정적 팩토리 메서드
   */
  public static ChannelNotFoundException withChannelId(UUID channelId) {
    return new ChannelNotFoundException(Map.of("channelId", channelId));
  }

  /**
   * 채널명으로 예외를 생성하는 정적 팩토리 메서드
   */
  public static ChannelNotFoundException withChannelName(String channelName) {
    return new ChannelNotFoundException(Map.of("channelName", channelName));
  }

  /**
   * 일반적인 생성 메서드
   */
  public static ChannelNotFoundException of() {
    return new ChannelNotFoundException();
  }

  /**
   * 상세 정보와 함께 생성하는 메서드
   */
  public static ChannelNotFoundException of(Map<String, Object> details) {
    return new ChannelNotFoundException(details);
  }
}