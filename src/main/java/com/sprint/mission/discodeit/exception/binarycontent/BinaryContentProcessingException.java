package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class BinaryContentProcessingException extends BinaryContentException {

  public BinaryContentProcessingException() {
    super(ErrorCode.BINARY_CONTENT_PROCESSING_ERROR, "BinaryContent 처리 중 오류가 발생했습니다.");
  }
}
