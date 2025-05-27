package com.sprint.mission.discodeit.dto.Channel;

import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record PrivateChannelCreateRequest(
        List<UUID> participantIds
) {
}
