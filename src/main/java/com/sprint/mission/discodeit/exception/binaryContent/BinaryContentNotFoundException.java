package com.sprint.mission.discodeit.exception.binaryContent;

import com.sprint.mission.discodeit.dto.ErrorCode;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class BinaryContentNotFoundException extends BinaryContentException {
    public BinaryContentNotFoundException(UUID binaryContentId) {
        super(
                Instant.now(),
                ErrorCode.BINARY_NOT_FOUND,
                Map.of("binaryContentId", binaryContentId.toString())
        );
    }
}
