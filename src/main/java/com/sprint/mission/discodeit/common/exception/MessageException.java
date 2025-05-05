package com.sprint.mission.discodeit.common.exception;

import java.util.UUID;

public class MessageException extends BusinessException {

  public static final String MESSAGE_NOT_FOUND = "M001";

  public MessageException(String errorCode, String message) {
    super(errorCode, message);
  }

  public static MessageException notFound(UUID messageId) {
    return new MessageException(MESSAGE_NOT_FOUND,
        "메시지를 찾을 수 없음습니다. [MessageID: " + messageId + "]");
  }
}