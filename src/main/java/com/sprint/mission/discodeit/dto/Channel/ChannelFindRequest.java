package com.sprint.mission.discodeit.dto.Channel;

import java.util.UUID;
import lombok.Builder;

@Builder
public record ChannelFindRequest(
        UUID id
) {
}
