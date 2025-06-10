package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.UserStatusResponse;
import com.sprint.mission.discodeit.entity.UserStatus;
import java.time.Instant;
import org.springframework.stereotype.Component;

@Component
public class UserStatusMapper {

  public boolean isOnline(UserStatus status) {
    return status != null &&
        status.getLastAccessedAt() != null &&
        status.getLastAccessedAt().isAfter(Instant.now().minusSeconds(300));
  }

  public UserStatusResponse toResponse(UserStatus entity) {
    return new UserStatusResponse(
        entity.getId(),
        entity.getCreatedAt(),
        entity.getUpdatedAt(),
        entity.getUser().getId(),
        entity.getLastAccessedAt(),
        isOnline(entity)
    );
  }
}
