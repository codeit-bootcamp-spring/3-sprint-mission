package com.sprint.mission.discodeit.exception;

import lombok.AllArgsConstructor;

import java.util.Map;

/**
 * PackageName  : com.sprint.mission.discodeit.exception
 * FileName     : ChannelNotFoundException
 * Author       : dounguk
 * Date         : 2025. 6. 18.
 */
public class ChannelNotFoundException extends ChannelException {
    public ChannelNotFoundException( Map<String, Object> details) {
        super(ErrorCode.CHANNEL_NOT_FOUND, details);
    }
    public ChannelNotFoundException() {
        super(ErrorCode.CHANNEL_NOT_FOUND);
    }
}
