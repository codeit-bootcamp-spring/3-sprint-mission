package com.sprint.mission.discodeit.dto.userstatus;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.time.Instant;
import java.util.UUID;

public record UserStatusRequestDTO(UUID userId, Instant lastLoginTime) {

    public static UserStatus fromDTO(UserStatusRequestDTO userStatusRequestDTO) {
        UserStatus userStatus = new UserStatus(userStatusRequestDTO.userId(),
                userStatusRequestDTO.lastLoginTime());

        return userStatus;
    }
}
