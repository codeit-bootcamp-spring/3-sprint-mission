package com.sprint.mission.discodeit.dto.User;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record UserDTO(
   UUID id,
   Instant createdAt,
   Instant updatedAt,
   String userName,
   String email,
   UUID profileId,
   boolean isOnline
){
    public static UserDTO from(User user, UserStatus userStatus) {
        return new UserDTO(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getUserName(),
                user.getEmail(),
                user.getProfileId(),
                userStatus.isOnline()
        );
    }
}
