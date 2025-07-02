package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public abstract class BinaryContentException extends DiscodeitException {

  protected BinaryContentException(ErrorCode errorCode, String reason) {
    super(errorCode, java.util.Map.of("reason", reason));
  }

  protected BinaryContentException(ErrorCode errorCode) {
    super(errorCode);
  }
}
