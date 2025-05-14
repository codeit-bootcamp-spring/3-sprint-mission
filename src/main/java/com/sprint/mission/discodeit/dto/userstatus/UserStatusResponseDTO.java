package com.sprint.mission.discodeit.dto.userstatus;

import com.sprint.mission.discodeit.entity.UserStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class UserStatusResponseDTO {

    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    private UUID userId;
    private Instant lastLoginTime;

    public UserStatusResponseDTO() {
    }

    public static UserStatusResponseDTO toDTO(UserStatus userStatus) {
        UserStatusResponseDTO userStatusResponseDTO = new UserStatusResponseDTO();

        userStatusResponseDTO.setId(userStatus.getId());
        userStatusResponseDTO.setCreatedAt(userStatus.getCreatedAt());
        userStatusResponseDTO.setUpdatedAt(userStatus.getUpdatedAt());
        userStatusResponseDTO.setUserId(userStatus.getUserId());
        userStatusResponseDTO.setLastLoginTime(userStatus.getLastLoginTime());

        return userStatusResponseDTO;
    }

    @Override
    public String toString() {
        return "UserStatusResponseDTO{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", userId=" + userId +
                ", lastLoginTime=" + lastLoginTime +
                '}';
    }
}
