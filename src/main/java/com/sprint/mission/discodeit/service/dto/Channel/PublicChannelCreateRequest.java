package com.sprint.mission.discodeit.service.dto.Channel;

import lombok.Builder;

@Builder
public record PublicChannelCreateRequest(
        String channelName,
        String description

) {
}
