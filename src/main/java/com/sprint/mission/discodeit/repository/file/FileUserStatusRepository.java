package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class FileUserStatusRepository implements UserStatusRepository {

  private final Map<UUID, UserStatus> store = new HashMap<>();

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
  public List<UserStatus> findAll() {
    return new ArrayList<>(store.values());
  }

  @Override
  public void delete(UUID id) {
    store.remove(id);
  }
}

