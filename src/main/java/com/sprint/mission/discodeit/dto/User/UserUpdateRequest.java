package com.sprint.mission.discodeit.dto.User;

import lombok.Builder;

@Builder
public record UserUpdateRequest(
        String newUsername,
        String newEmail,
        String newPassword
) {
}