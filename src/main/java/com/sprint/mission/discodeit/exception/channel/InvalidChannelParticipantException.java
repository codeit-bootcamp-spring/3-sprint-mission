package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class InvalidChannelParticipantException extends ChannelException {

    public InvalidChannelParticipantException(UUID channelId, List<UUID> invalidUserIds) {
        super(ErrorCode.INVALID_CHANNEL_PARTICIPANT,
            Map.of("channelId", channelId, "invalidUserIds", invalidUserIds));
    }
}
