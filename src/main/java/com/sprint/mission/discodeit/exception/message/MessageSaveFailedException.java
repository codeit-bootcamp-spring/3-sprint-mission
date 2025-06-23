package com.sprint.mission.discodeit.exception.message;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class MessageSaveFailedException extends MessageException {
  public MessageSaveFailedException(String messageId) {
    super(ErrorCode.MESSAGE_PROCESSING_ERROR, "메시지 저장에 실패했습니다. [MessageID: " + messageId + "]");
  }
}
