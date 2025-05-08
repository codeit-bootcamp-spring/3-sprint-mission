package com.sprint.mission.discodeit.dto.User;

import java.util.UUID;
import lombok.Builder;

@Builder
public record UserResponse(
        UUID id,
        String username,
        String email,
        boolean isOnline,
        boolean hasProfileImage
) {
}