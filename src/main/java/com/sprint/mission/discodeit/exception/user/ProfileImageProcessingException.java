package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class ProfileImageProcessingException extends UserException {

    public ProfileImageProcessingException(String filename) {
        super(ErrorCode.PROFILE_IMAGE_PROCESSING_FAILED, Map.of("filename", filename));
    }
}
