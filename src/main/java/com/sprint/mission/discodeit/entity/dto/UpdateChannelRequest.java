package com.sprint.mission.discodeit.entity.dto;

import java.util.UUID;

public record UpdateChannelRequest(
        UUID channelId,
        String newName,
        String newDescription
) {
}
