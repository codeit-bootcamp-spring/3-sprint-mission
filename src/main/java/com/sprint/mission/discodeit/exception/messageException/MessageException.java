package com.sprint.mission.discodeit.exception.messageException;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

/**
 * PackageName  : com.sprint.mission.discodeit.exception
 * FileName     : MessageException
 * Author       : dounguk
 * Date         : 2025. 6. 20.
 */
public class MessageException extends DiscodeitException {
    public MessageException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }

    public MessageException(ErrorCode errorCode) {
        super(errorCode);
    }
}
