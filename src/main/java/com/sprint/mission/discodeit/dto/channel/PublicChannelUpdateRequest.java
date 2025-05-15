package com.sprint.mission.discodeit.dto.channel;

import java.util.UUID;

public record PublicChannelUpdateRequest(
        UUID channelId,
        String channelName,
        String password
) {
}
