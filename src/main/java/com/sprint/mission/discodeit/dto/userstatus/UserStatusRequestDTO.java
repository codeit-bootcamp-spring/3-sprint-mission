package com.sprint.mission.discodeit.dto.userstatus;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.time.Instant;
import java.util.UUID;

public record UserStatusRequestDTO(UUID userId, Instant lastLoginTime) {

    public static UserStatus toEntity(UserStatusRequestDTO userStatusRequestDTO) {
        UserStatus userStatus = new UserStatus(userStatusRequestDTO.userId());

        userStatus.updateLastLoginTime(userStatusRequestDTO.lastLoginTime());

        return userStatus;
    }
}
