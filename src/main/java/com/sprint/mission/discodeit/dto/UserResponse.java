package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.UUID;

public record UserResponse(UUID userId, String name, String email, UUID profileId,
                           UserStatus userStatus) {
}
