package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserOnlineService {

  private final JwtRegistry jwtRegistry;

  public boolean isOnline(UUID userId) {
    return jwtRegistry.hasActiveJwtInformationByUserId(userId);
  }

  public Map<UUID, Boolean> bulkIsOnline(Collection<UUID> userIds) {
    Map<UUID, Boolean> result = new HashMap<>();
    userIds.forEach(id -> result.put(id, jwtRegistry.hasActiveJwtInformationByUserId(id)));
    return result;
  }
}
