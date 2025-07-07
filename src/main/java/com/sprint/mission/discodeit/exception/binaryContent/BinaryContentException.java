package com.sprint.mission.discodeit.exception.binaryContent;

import com.sprint.mission.discodeit.dto.ErrorCode;
import com.sprint.mission.discodeit.exception.DiscodeitException;

import java.time.Instant;
import java.util.Map;

public class BinaryContentException extends DiscodeitException {
    public BinaryContentException(Instant timestamp, ErrorCode errorCode, Map<String, Object> details) {
        super(timestamp, errorCode, details);
    }
}
