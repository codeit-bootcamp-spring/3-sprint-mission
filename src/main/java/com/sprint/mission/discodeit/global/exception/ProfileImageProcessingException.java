package com.sprint.mission.discodeit.global.exception;

import static com.sprint.mission.discodeit.global.constant.ErrorMessages.PROFILE_IMAGE_PROCESSING_FAILED;

public class ProfileImageProcessingException extends RuntimeException {

  public ProfileImageProcessingException(String filename) {
    super(String.format(PROFILE_IMAGE_PROCESSING_FAILED, filename));
  }
}