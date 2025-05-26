package com.sprint.mission.discodeit.dto.request;

import java.util.Optional;
import java.util.UUID;

public record UserUpdateRequest(
        String newUsername,
        String newEmail,
        String newPassword
) {
}
