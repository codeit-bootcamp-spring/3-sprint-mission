package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class CursorInvalidException extends ChannelException {

    public CursorInvalidException(String cursorValue) {
        super(ErrorCode.CURSOR_INVALID, Map.of("invalidCursor", cursorValue));

    }

}
