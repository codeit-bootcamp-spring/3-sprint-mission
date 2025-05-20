package com.sprint.mission.discodeit.global.exception;

import static com.sprint.mission.discodeit.global.constant.ErrorMessages.BINARY_CONTENT_NOT_FOUND;

public class BinaryContentNotFoundException extends RuntimeException {

  public BinaryContentNotFoundException(String id) {
    super(String.format(BINARY_CONTENT_NOT_FOUND, id));
  }
}
