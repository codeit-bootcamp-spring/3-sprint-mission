package com.sprint.mission.discodeit.dto.ReadStatus;

import java.time.Instant;

public record ReadStatusUpdateRequest(
        Instant newLastReadAt
) {

}
