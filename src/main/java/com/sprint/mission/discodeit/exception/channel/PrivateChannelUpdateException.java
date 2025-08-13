package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

/**
 * 비공개 채널 수정 불가 등 정책 위반 시 발생하는 예외입니다.
 */
public class PrivateChannelUpdateException extends ChannelException {
    public PrivateChannelUpdateException(String message) {
        super(message, ErrorCode.PRIVATE_CHANNEL_UPDATE_NOT_ALLOWED);
    }
    public PrivateChannelUpdateException(String message, Map<String, Object> details) {
        super(message, ErrorCode.PRIVATE_CHANNEL_UPDATE_NOT_ALLOWED, details);
    }
} 