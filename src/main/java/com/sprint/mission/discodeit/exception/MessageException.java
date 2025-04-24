package com.sprint.mission.discodeit.exception;

import java.util.UUID;

public class MessageException extends BusinessException {

  public static final String MESSAGE_NOT_FOUND = "M001";

  public MessageException(String message, String errorCode) {
    super(message, errorCode);
  }

  public static MessageException notFound(UUID messageId) {
    return new MessageException(MESSAGE_NOT_FOUND,
        "메시지를 찾을 수 없음습니다. [MessageID: " + messageId + "]");
  }
}