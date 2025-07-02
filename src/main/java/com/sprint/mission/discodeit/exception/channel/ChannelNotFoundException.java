package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

/**
 * 채널이 존재하지 않을 때 발생하는 예외입니다.
 */
public class ChannelNotFoundException extends ChannelException {
    public ChannelNotFoundException(String message) {
        super(message, ErrorCode.CHANNEL_NOT_FOUND);
    }
    public ChannelNotFoundException(String message, Map<String, Object> details) {
        super(message, ErrorCode.CHANNEL_NOT_FOUND, details);
    }
    public ChannelNotFoundException(String message, Object channelId) {
        super(message, ErrorCode.CHANNEL_NOT_FOUND, Map.of("channelId", channelId));
    }
} 