package com.sprint.mission.discodeit.dto.data;

import java.util.UUID;

public record UserDto (
        UUID id,
        String userName,
        String email,
        boolean isOnline,
        UUID profileId
) {}
