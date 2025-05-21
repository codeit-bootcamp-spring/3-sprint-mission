package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
@Repository
public class JcfReadStatusRepository implements ReadStatusRepository {

  private final Map<UUID, ReadStatus> storage = new ConcurrentHashMap<>();

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
  public synchronized void deleteByUserIdAndChannelId(UUID userId, UUID channelId) {
    /*
    find와 remove 사이에 다른 스레드가 상태를 변경할 수 있다.
    동기화(synchronized) - find와 remove가 한 번에 실행되도록 보장 || ConcurrentHashMap의 removeIf
     */
    Optional<ReadStatus> readStatus = findByUserIdAndChannelId(userId, channelId);
    readStatus.ifPresent(rs -> storage.remove(rs.getId()));
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


