package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.UUID;

public record PublicChannelCreateRequest(
        String name,
        String description
) {
}
