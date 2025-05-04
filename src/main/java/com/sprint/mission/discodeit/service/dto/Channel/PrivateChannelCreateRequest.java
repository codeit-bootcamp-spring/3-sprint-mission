package com.sprint.mission.discodeit.service.dto.Channel;

import java.util.List;
import java.util.UUID;

public record PrivateChannelCreateRequest(
        List<UUID> participantsIds
) {
}
