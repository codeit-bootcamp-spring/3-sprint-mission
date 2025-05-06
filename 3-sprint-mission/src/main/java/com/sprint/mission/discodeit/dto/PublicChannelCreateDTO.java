package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record PublicChannelCreateDTO(
        UUID userId,
        String channalName,
        boolean isPriavate
) {
}
