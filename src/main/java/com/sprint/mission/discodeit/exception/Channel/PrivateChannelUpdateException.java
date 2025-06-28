package com.sprint.mission.discodeit.exception.Channel;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;

@Getter
public class PrivateChannelUpdateException extends ChannelException {

    private final UUID channelId;

    public PrivateChannelUpdateException(UUID channelId) {
        super(
                ErrorCode.PRIVATE_CHANNEL_UPDATE,
                Map.of("channelId", channelId)
        );
        this.channelId = channelId;
    }
}
