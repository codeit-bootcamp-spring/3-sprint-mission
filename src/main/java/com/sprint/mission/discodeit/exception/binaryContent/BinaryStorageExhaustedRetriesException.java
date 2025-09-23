package com.sprint.mission.discodeit.exception.binaryContent;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class BinaryStorageExhaustedRetriesException extends BinaryContentException {

    public BinaryStorageExhaustedRetriesException(UUID binaryContentId) {
        super(ErrorCode.UPLOAD_IMAGE_S3_RETRIED_FAILED, Map.of("binaryContentId", binaryContentId));
    }
}
