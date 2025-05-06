package com.sprint.mission.discodeit.dto.data;

import com.sprint.mission.discodeit.entity.User;
import java.util.UUID;

public record UserDto(
        UUID id,
        String username,
        String email,
        UUID profileId,
        boolean isConnected
) {
    public UserDto(User user, boolean isConnected) {
        this(user.getId(), user.getUsername(), user.getEmail(), user.getProfileId(), isConnected);
    }
}
