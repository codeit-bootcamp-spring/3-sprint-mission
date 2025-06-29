package com.sprint.mission.discodeit.exception.message;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

/**
 * 메시지를 찾을 수 없을 때 발생하는 예외
 */
public class MessageNotFoundException extends MessageException {

  public MessageNotFoundException() {
    super(ErrorCode.MESSAGE_NOT_FOUND);
  }

  public MessageNotFoundException(Map<String, Object> details) {
    super(ErrorCode.MESSAGE_NOT_FOUND, details);
  }

  public MessageNotFoundException(Throwable cause) {
    super(ErrorCode.MESSAGE_NOT_FOUND, cause);
  }

  public MessageNotFoundException(Map<String, Object> details, Throwable cause) {
    super(ErrorCode.MESSAGE_NOT_FOUND, details, cause);
  }

  // === 편의 정적 팩토리 메서드들 ===

  /**
   * 메시지 ID로 예외를 생성하는 정적 팩토리 메서드
   */
  public static MessageNotFoundException withMessageId(UUID messageId) {
    return new MessageNotFoundException(Map.of("messageId", messageId));
  }

  /**
   * 채널 ID와 메시지 ID로 예외를 생성하는 정적 팩토리 메서드
   */
  public static MessageNotFoundException withChannelIdAndMessageId(UUID channelId, UUID messageId) {
    return new MessageNotFoundException(Map.of(
        "channelId", channelId,
        "messageId", messageId));
  }

  /**
   * 일반적인 생성 메서드
   */
  public static MessageNotFoundException of() {
    return new MessageNotFoundException();
  }

  /**
   * 상세 정보와 함께 생성하는 메서드
   */
  public static MessageNotFoundException of(Map<String, Object> details) {
    return new MessageNotFoundException(details);
  }
}