package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public record UserResponse(
    UUID id,
    Instant createdAt,
    Instant updatedAt,
    String email,
    String name,
    boolean isActive,
    UUID profileImageId
) {

  // 정적 팩토리 메서드
  public static UserResponse from(User user, Optional<UserStatus> userStatus) {
    boolean isActive = userStatus.map(UserStatus::isOnline).orElse(false);
    return new UserResponse(
        user.getId(),
        user.getCreatedAt(),
        user.getUpdatedAt(),
        user.getEmail(),
        user.getName(),
        isActive,
        user.getProfileImageId()
    );
  }
}