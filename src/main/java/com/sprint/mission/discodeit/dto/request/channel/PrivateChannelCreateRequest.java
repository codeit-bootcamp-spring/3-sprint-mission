package com.sprint.mission.discodeit.dto.request.channel;

import java.util.List;
import java.util.UUID;

public record PrivateChannelCreateRequest(
        List<UUID> participantIds
) {
}
