package com.sprint.mission.discodeit.dto;

import java.time.Instant;

public record ReadStatusUpdateDTO(
        Instant lastReadAt
) {
}
