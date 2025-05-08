package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.entity.User;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record UserDTO(UUID id,
                      Instant cratedAt,
                      Instant updatedAt,
                      String username,
                      String email,
                      boolean online) {

    public static UserDTO fromDomain(User user, boolean isOnline) {
        return UserDTO.builder()
                .id(user.getId())
                .cratedAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .username(user.getUsername())
                .email(user.getEmail())
                .online(isOnline)
                .build();

    }
}
