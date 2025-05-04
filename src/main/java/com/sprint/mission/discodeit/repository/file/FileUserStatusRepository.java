package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class FileUserStatusRepository implements UserStatusRepository {

  private final Map<UUID, UserStatus> store = new HashMap<>();

  @Override
  public UserStatus save(UserStatus status) {
    store.put(status.getUserId(), status);
    return status;
  }

  @Override
  public Optional<UserStatus> find(UUID userId) {
    return Optional.ofNullable(store.get(userId));
  }

  @Override
  public void delete(UUID userId) {
    store.remove(userId); // 삭제
  }
}
