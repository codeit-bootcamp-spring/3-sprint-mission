package com.sprint.mission.discodeit.exception;

import lombok.AllArgsConstructor;

import java.time.Instant;
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
