package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.entity.ErrorCode;
import com.sprint.mission.discodeit.exception.DiscodeitException;

import java.util.Map;

public class ChannelException extends DiscodeitException {

    public ChannelException(String message, ErrorCode errorCode, Map<String, Object> details) {
        super(message, errorCode, details);
    }
}
