package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
@Repository
public class JcfReadStatusRepository implements ReadStatusRepository {

  private final Map<UUID, ReadStatus> storage = new HashMap<>();

  @Override
  public ReadStatus save(ReadStatus readStatus) {
    storage.put(readStatus.getId(), readStatus);
    return readStatus;
  }

  @Override
  public Optional<ReadStatus> findById(UUID id) {
    return Optional.ofNullable(storage.get(id));
  }

  @Override
  public Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId) {
    return storage.values().stream()
        .filter(rs -> rs.getUserId().equals(userId) && rs.getChannelId().equals(channelId))
        .findFirst();
  }

  @Override
  public List<ReadStatus> findAllByUserId(UUID userId) {
    return storage.values().stream()
        .filter(rs -> rs.getUserId().equals(userId))
        .collect(Collectors.toList());
  }

  @Override
  public List<ReadStatus> findByChannelId(UUID channelId) {
    return storage.values().stream()
        .filter(rs -> rs.getChannelId().equals(channelId))
        .collect(Collectors.toList());
  }

  @Override
  public void updateLastReadAt(UUID id, Instant newReadAt) {
    ReadStatus rs = storage.get(id);
    if (rs != null) {
      rs.updateLastReadAt(newReadAt);
    }
  }

  @Override
  public void deleteById(UUID id) {
    storage.remove(id);
  }

  @Override
  public void deleteAll(List<ReadStatus> readStatuses) {
    for (ReadStatus readStatus : readStatuses) {
      storage.remove(readStatus.getId());
    }
  }
}


