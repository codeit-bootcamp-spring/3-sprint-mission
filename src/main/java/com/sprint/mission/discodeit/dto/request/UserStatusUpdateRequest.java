package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record UserStatusUpdateRequest(
    @NotNull(message = "lastActiveAt은 필수입니다")
    Instant newLastActiveAt
) {

}
