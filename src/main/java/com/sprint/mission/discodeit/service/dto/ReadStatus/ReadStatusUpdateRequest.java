package com.sprint.mission.discodeit.service.dto.ReadStatus;

import java.time.Instant;
import java.util.UUID;

public record ReadStatusUpdateRequest(
        UUID id,
        Instant newReadAt
) {

}
