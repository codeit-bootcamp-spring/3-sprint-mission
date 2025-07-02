package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class PrivateChannelModificationException extends ChannelException {

    public PrivateChannelModificationException(UUID channelId) {
        super(ErrorCode.PRIVATE_CHANNEL_MODIFICATION, Map.of("channelId", channelId));
    }
}
