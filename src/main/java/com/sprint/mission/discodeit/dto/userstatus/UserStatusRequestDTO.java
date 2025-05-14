package com.sprint.mission.discodeit.dto.userstatus;

import com.sprint.mission.discodeit.entity.UserStatus;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatusRequestDTO {

    private UUID userId;
    private Instant lastLoginTime;

    public UserStatusRequestDTO(UUID userId, Instant lastLoginTime) {
        this.userId = userId;
        this.lastLoginTime = lastLoginTime;
    }

    public static UserStatus toEntity(UserStatusRequestDTO userStatusRequestDTO) {
        UserStatus userStatus = new UserStatus(userStatusRequestDTO.getUserId());

        userStatus.updateLastLoginTime(userStatusRequestDTO.getLastLoginTime());

        return userStatus;
    }
}
