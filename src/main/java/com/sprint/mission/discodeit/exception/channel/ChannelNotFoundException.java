package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class ChannelNotFoundException extends ChannelException {
    public ChannelNotFoundException(Object details) {
        super(ErrorCode.CHANNEL_NOT_FOUND, details);
    }
}
