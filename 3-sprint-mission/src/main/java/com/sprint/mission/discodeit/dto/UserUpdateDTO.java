package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record UserUpdateDTO(
        UUID id,
        String newName,
        String newEmail,
        String newPassword,
        UUID profileId
) {
}
