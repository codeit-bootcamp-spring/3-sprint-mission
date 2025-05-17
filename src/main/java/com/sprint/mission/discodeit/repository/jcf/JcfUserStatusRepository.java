package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
@Repository
public class JcfUserStatusRepository implements UserStatusRepository {

  private final Map<UUID, UserStatus> store = new ConcurrentHashMap<>();

  @Override
  public UserStatus save(UserStatus status) {
    store.put(status.getId(), status);
    return status;
  }

  @Override
  public Optional<UserStatus> find(UUID id) {
    return Optional.ofNullable(store.get(id));
  }

  @Override
  public Optional<UserStatus> findByUserId(UUID userId) {
    return store.values().stream()
        .filter(status -> status.getUserId().equals(userId))
        .findFirst();
  }

  @Override
  public List<UserStatus> findAll() {
    return new ArrayList<>(store.values());
  }

  @Override
  public void delete(UUID id) {
    store.remove(id);
  }
}

