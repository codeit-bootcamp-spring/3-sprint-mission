package com.sprint.mission.discodeit.exception;

import java.util.Map;

/**
 * PackageName  : com.sprint.mission.discodeit.exception
 * FileName     : MessageNotFoundException
 * Author       : dounguk
 * Date         : 2025. 6. 20.
 */
public class MessageNotFoundException extends MessageException {
    public MessageNotFoundException(Map<String, Object> details) {
        super(ErrorCode.MESSAGE_NOT_FOUND, details);
    }
    public MessageNotFoundException() {
        super(ErrorCode.MESSAGE_NOT_FOUND);
    }
}
