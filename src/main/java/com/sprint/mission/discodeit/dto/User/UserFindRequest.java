package com.sprint.mission.discodeit.dto.User;

import java.util.UUID;
import lombok.Builder;

@Builder
public record UserFindRequest(
        UUID userId
) {
}
