package com.sprint.mission.discodeit.dto.channel;

import java.util.UUID;

public record ChannelMemberRequestDTO(UUID channelId, UUID userId) {
}
