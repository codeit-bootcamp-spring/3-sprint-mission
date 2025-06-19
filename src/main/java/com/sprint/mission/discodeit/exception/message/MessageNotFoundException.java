package com.sprint.mission.discodeit.exception.message;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class MessageNotFoundException extends MessageException {
  public MessageNotFoundException(String messageId) {
    super(ErrorCode.MESSAGE_NOT_FOUND, "메시지를 찾을 수 없습니다. [MessageID: " + messageId + "]");
  }
}
