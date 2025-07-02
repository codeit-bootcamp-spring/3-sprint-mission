package com.sprint.mission.discodeit.exception.message;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

/**
 * 메시지 권한 위반(수정/삭제 등) 시 발생하는 예외입니다.
 */
public class MessagePermissionException extends MessageException {
    public MessagePermissionException(String message) {
        super(message, ErrorCode.MESSAGE_PERMISSION_DENIED);
    }
    public MessagePermissionException(String message, Map<String, Object> details) {
        super(message, ErrorCode.MESSAGE_PERMISSION_DENIED, details);
    }
} 