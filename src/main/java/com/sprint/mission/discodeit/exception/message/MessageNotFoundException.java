package com.sprint.mission.discodeit.exception.message;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

/**
 * 메시지가 존재하지 않을 때 발생하는 예외입니다.
 */
public class MessageNotFoundException extends MessageException {
    public MessageNotFoundException(String message) {
        super(message, ErrorCode.MESSAGE_NOT_FOUND);
    }
    public MessageNotFoundException(String message, Map<String, Object> details) {
        super(message, ErrorCode.MESSAGE_NOT_FOUND, details);
    }
    public MessageNotFoundException(String message, Object messageId) {
        super(message, ErrorCode.MESSAGE_NOT_FOUND, Map.of("messageId", messageId));
    }
} 