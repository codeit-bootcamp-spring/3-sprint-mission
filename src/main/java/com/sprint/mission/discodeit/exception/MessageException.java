package com.sprint.mission.discodeit.exception;

import java.util.UUID;

public class MessageException extends BusinessException {

  public MessageException(ErrorCode errorCode, String message) {
    super(errorCode, message);
  }

  public static MessageException notFound(UUID messageId) {
    return new MessageException(
        ErrorCode.NOT_FOUND,
        "메시지를 찾을 수 없습니다. [MessageID: " + messageId + "]"
    );
  }

  public static MessageException saveFailed(UUID messageId) {
    return new MessageException(
        ErrorCode.PROCESSING_ERROR,
        "메시지 저장에 실패했습니다. [MessageID: " + messageId + "]"
    );
  }
}