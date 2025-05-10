package com.sprint.mission.discodeit.dto.Channel;

import lombok.Builder;

@Builder
public record PublicChannelCreateRequest(
        String channelName,
        String description

) {
}
