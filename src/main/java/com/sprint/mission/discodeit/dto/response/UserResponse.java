package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.User;
import java.time.Instant;
import java.util.UUID;

public record UserResponse(
    UUID id,
    Instant createdAt,
    Instant updatedAt,
    String username,
    String email,
    BinaryContentResponse profile,
    boolean online
) {

  public static UserResponse from(User user, Boolean isOnline) {
    return new UserResponse(
        user.getId(),
        user.getCreatedAt(),
        user.getUpdatedAt(),
        user.getUsername(),
        user.getEmail(),
        user.getProfile() != null ? BinaryContentResponse.from(user.getProfile()) : null,
        Boolean.TRUE.equals(isOnline)
    );
  }
}