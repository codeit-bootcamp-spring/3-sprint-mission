package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.entity.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class PrivateChannelUpdateException extends ChannelException {

    public PrivateChannelUpdateException(UUID channelId) {
        super(
                ErrorCode.PRIVATE_CHANNEL_UPDATE.getMessage(),
                ErrorCode.PRIVATE_CHANNEL_UPDATE,
                Map.of("channelId", "channelId")
        );
    }
}
