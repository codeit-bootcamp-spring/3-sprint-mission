package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class JCFUserStatusRepository implements UserStatusRepository {

  private final Map<UUID, UserStatus> userStatusMap = new ConcurrentHashMap<>();
  private final Map<UUID, UUID> userIdToStatusIdMap = new ConcurrentHashMap<>();

  @Override
  public Optional<UserStatus> findById(UUID id) {
    return Optional.ofNullable(userStatusMap.get(id));
  }

  @Override
  public Optional<UserStatus> findByUserId(UUID userId) {
    return Optional.ofNullable(userIdToStatusIdMap.get(userId))
        .flatMap(this::findById);
  }

  @Override
  public List<UserStatus> findAll() {
    return List.copyOf(userStatusMap.values());
  }

  @Override
  public UserStatus save(UserStatus userStatus) {
    userStatusMap.put(userStatus.getId(), userStatus);
    userIdToStatusIdMap.put(userStatus.getUserId(), userStatus.getId());
    return userStatus;
  }

  @Override
  public void delete(UUID id) {
    Optional.ofNullable(userStatusMap.get(id))
        .ifPresent(status -> {
          userStatusMap.remove(id);
          userIdToStatusIdMap.remove(status.getUserId());
        });
  }
}