package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.*;

@ConditionalOnProperty(name = "discodeit.repository", havingValue = "jcf")
@Repository
public class JCFReadStatusRepository implements ReadStatusRepository {

  private final Map<UUID, ReadStatus> readStatusData;

  public JCFReadStatusRepository() {
    this.readStatusData = new HashMap<>();
  }

  @Override
  public ReadStatus save(ReadStatus readStatus) {
    this.readStatusData.put(readStatus.getId(), readStatus);
    return readStatus;
  }

  @Override
  public Optional<ReadStatus> findById(UUID id) {
    return Optional.ofNullable(this.readStatusData.get(id));
  }


  @Override
  public List<ReadStatus> findAllByUserId(UUID userId) {
    return this.readStatusData.values()
        .stream()
        .filter(readStatus -> readStatus.getUserId().equals(userId))
        .toList();
  }

  @Override
  public List<ReadStatus> findAllByChannelId(UUID channelId) {
    return this.readStatusData.values().stream()
        .filter(readStatus -> readStatus.getChannelId().equals(channelId))
        .toList();
  }

  @Override
  public boolean existsById(UUID id) {
    return this.readStatusData.containsKey(id);
  }

  @Override
  public void deleteById(UUID id) {
    this.readStatusData.remove(id);
  }

  @Override
  public void deleteByChannelId(UUID channelId) {
    readStatusData.values().removeIf(readStatus -> readStatus.getChannelId().equals(channelId));
  }
}
