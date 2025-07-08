package com.sprint.mission.discodeit.exception.message;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class MessageEmptyException extends MessageException {

    public MessageEmptyException() {
        super(ErrorCode.MESSAGE_EMPTY, Map.of());
    }

}
