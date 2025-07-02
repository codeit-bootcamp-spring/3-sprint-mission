package com.sprint.mission.discodeit.exception.Channel;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;

@Getter
public class ChannelNotFoundException extends ChannelException {

    private final UUID channelId;

    public ChannelNotFoundException(UUID channelId) {
        super(
                ErrorCode.CHANNEL_NOT_FOUND,
                Map.of("channelId", channelId)
        );
        this.channelId = channelId;
    }
}
