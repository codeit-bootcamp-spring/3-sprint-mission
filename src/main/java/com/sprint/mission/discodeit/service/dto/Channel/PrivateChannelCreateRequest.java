package com.sprint.mission.discodeit.service.dto.Channel;

import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record PrivateChannelCreateRequest(
        List<UUID> participantsIds
) {
}
