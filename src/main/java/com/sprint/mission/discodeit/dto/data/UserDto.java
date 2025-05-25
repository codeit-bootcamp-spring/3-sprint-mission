package com.sprint.mission.discodeit.dto.data;

import com.sprint.mission.discodeit.entity.User;
import java.time.Instant;
import java.util.UUID;

public record UserDto(
    UUID id,
    Instant createdAt,
    Instant updatedAt,
    String username,
    String email,
    UUID profileId
) {

  public static UserDto fromEntity(User user) {
    if (user == null) {
      return null;
    }

    return new UserDto(
        user.getId(),
        user.getCreatedAt(),
        user.getUpdatedAt(),
        user.getUsername(),
        user.getEmail(),
        user.getPortraitId()
    );
  }
}
