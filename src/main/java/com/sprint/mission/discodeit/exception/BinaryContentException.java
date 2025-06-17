package com.sprint.mission.discodeit.exception;

import java.text.MessageFormat;
import java.util.UUID;

public class BinaryContentException extends BusinessException {

  public BinaryContentException(ErrorCode errorCode) {
    super(errorCode, errorCode.getMessage());
  }

  public BinaryContentException(ErrorCode errorCode, String message) {
    super(errorCode, message);
  }

  public static BinaryContentException notFound() {
    return new BinaryContentException(ErrorCode.NOT_FOUND,
        "BinaryContent를 찾을 수 없습니다."
    );
  }

  public static BinaryContentException notFound(UUID id) {
    return new BinaryContentException(ErrorCode.NOT_FOUND,
        MessageFormat.format("BinaryContent를 찾을 수 없습니다. [id: {0}]", id)
    );
  }

  public static BinaryContentException invalidRequest() {
    return new BinaryContentException(ErrorCode.INVALID_INPUT);
  }

  public static BinaryContentException processingError() {
    return new BinaryContentException(ErrorCode.PROCESSING_ERROR);
  }
}
