package com.sprint.mission.discodeit.exception.channelException;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

/**
 * PackageName  : com.sprint.mission.discodeit.exception
 * FileName     : ChannelException
 * Author       : dounguk
 * Date         : 2025. 6. 18.
 */

public class ChannelException extends DiscodeitException {
    public ChannelException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
    public ChannelException(ErrorCode errorCode) {
        super(errorCode);
    }
}
