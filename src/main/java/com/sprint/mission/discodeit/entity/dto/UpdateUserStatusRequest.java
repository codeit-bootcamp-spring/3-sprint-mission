package com.sprint.mission.discodeit.entity.dto;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.time.Instant;

public record UpdateUserStatusRequest(
        User user,
        UserStatus userStatus,
        Instant lastLoginAt
) {
}
