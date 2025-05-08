package com.sprint.mission.discodeit.service.dto.User;

import java.util.UUID;
import lombok.Builder;

@Builder
public record UserUpdateRequest(
        UUID userId,
        String newUsername,
        String newEmail,
        String newPassword,
        boolean hasProfileImage,
        byte[] newProfileImage,
        String newProfileContentType
) {
}