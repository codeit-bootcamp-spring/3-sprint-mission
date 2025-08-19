package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.security.userdetails.DiscodeitUserDetails;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserOnlineService {

  private final SessionRegistry sessionRegistry;

  /**
   * 단일 사용자 온라인 여부
   */
  public boolean isOnline(UUID userId) {
    return sessionRegistry.getAllPrincipals().stream()
        .filter(p -> p instanceof DiscodeitUserDetails)
        .map(p -> (DiscodeitUserDetails) p)
        .filter(ud -> ud.getUser().id().equals(userId))
        .flatMap(ud -> sessionRegistry.getAllSessions(ud, false).stream())
        .anyMatch(si -> !si.isExpired());
  }

  /**
   * 여러 사용자 온라인 여부를 한 번의 principals 순회로 계산
   */
  public Map<UUID, Boolean> bulkIsOnline(Collection<UUID> userIds) {
    Set<UUID> targets =
        (userIds instanceof Set<?> && userIds.stream().allMatch(e -> e instanceof UUID))
            ? (Set<UUID>) userIds
            : userIds.stream().collect(Collectors.toSet());
    Map<UUID, Boolean> result = new HashMap<>();
    targets.forEach(id -> result.put(id, false));

    sessionRegistry.getAllPrincipals().stream()
        .filter(p -> p instanceof DiscodeitUserDetails)
        .map(p -> (DiscodeitUserDetails) p)
        .filter(ud -> targets.contains(ud.getUser().id()))
        .forEach(ud -> {
          boolean hasActive = sessionRegistry.getAllSessions(ud, false).stream()
              .anyMatch(si -> !si.isExpired());
          if (hasActive) {
            result.put(ud.getUser().id(), true);
          }
        });
    return result;
  }
}
