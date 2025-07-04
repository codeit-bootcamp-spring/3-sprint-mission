package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.dto.ErrorCode;
import com.sprint.mission.discodeit.exception.DiscodeitException;

import java.time.Instant;
import java.util.Map;

public class ChannelException extends DiscodeitException {
    public ChannelException(Instant timestamp, ErrorCode errorCode, Map<String, Object> details) {
        super(timestamp, errorCode, details);
    }
}
