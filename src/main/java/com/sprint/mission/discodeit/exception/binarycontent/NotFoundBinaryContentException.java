package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.entity.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class NotFoundBinaryContentException extends BinaryContentException {

    public NotFoundBinaryContentException(UUID id) {
        super(
                ErrorCode.BINARY_CONTENT_NOT_FOUND.getMessage() + " 파일 id: " + id,
                ErrorCode.BINARY_CONTENT_NOT_FOUND,
                Map.of("binaryContentId", id)
        );
    }
}
