package com.sprint.mission.discodeit.dto;

import java.util.List;
import java.util.UUID;

public record PrivateChannelCreateDTO(
        UUID userId,
        String channalName,
        List<UUID> entry,
        boolean isPriavate
) {
}
