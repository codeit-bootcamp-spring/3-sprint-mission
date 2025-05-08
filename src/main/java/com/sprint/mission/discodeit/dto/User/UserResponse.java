package com.sprint.mission.discodeit.dto.User;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        String email,
        boolean isOnline,
        boolean profileImage
) {
}