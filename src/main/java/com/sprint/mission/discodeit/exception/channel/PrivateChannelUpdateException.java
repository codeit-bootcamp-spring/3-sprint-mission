package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.dto.response.ErrorCode;
import com.sprint.mission.discodeit.entity.ChannelType;
import java.time.Instant;
import java.util.Map;

public class PrivateChannelUpdateException extends ChannelException {

    public PrivateChannelUpdateException(ChannelType channelType) {
        super(Instant.now(), ErrorCode.PRIVATE_CHANNEL_UPDATE, Map.of("channelType", channelType));
    }
}