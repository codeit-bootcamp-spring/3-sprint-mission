package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.entity.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class NotFoundChannelException extends ChannelException {

    public NotFoundChannelException(UUID channelId) {
        super(
                ErrorCode.CHANNEL_NOT_FOUND.getMessage() + " channelId: " + channelId,
                ErrorCode.CHANNEL_NOT_FOUND,
                Map.of("channelId", channelId)
        );
    }
}
