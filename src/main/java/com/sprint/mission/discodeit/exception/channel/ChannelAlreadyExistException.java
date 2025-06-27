package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class ChannelAlreadyExistException extends ChannelException {

    public ChannelAlreadyExistException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
}
