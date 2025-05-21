package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.AbstractFileRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
@Repository
public class FileReadStatusRepository extends AbstractFileRepository<ReadStatus, UUID> implements ReadStatusRepository {

  public FileReadStatusRepository(
      @Value("${discodeit.repository.file-directory}") String fileDirectory
  ) {
    super(Paths.get(System.getProperty("user.dir"), fileDirectory, "readStatus.ser").toString());
  }

  @Override
  public ReadStatus save(ReadStatus readStatus) {
    return super.save(readStatus, readStatus.getId());
  }

  @Override
  public Optional<ReadStatus> findById(UUID id) {
    return super.findById(id);
  }

  @Override
  public Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId) {
    return super.findAll().stream()
        .filter(rs -> rs.getUserId().equals(userId) && rs.getChannelId().equals(channelId))
        .findFirst();
  }

  @Override
  public List<ReadStatus> findAllByUserId(UUID userId) {
    return super.findAll().stream()
        .filter(rs -> rs.getUserId().equals(userId))
        .collect(Collectors.toList());
  }

  @Override
  public List<ReadStatus> findByChannelId(UUID channelId) {
    return super.findAll().stream()
        .filter(rs -> rs.getChannelId().equals(channelId))
        .collect(Collectors.toList());
  }

  @Override
  public void updateLastReadAt(UUID id, Instant newReadAt) {
    ReadStatus rs = super.findById(id).orElse(null);
    if (rs != null) {
      rs.updateLastReadAt(newReadAt);
      super.save(rs, id);
    }
  }

  @Override
  public void deleteByUserIdAndChannelId(UUID userId, UUID channelId) {
    lock.lock();
    try {
      List<ReadStatus> readStatusList = super.findAll().stream()
          .filter(rs -> rs.getUserId().equals(userId) && rs.getChannelId().equals(channelId))
          .collect(Collectors.toList());

      for (ReadStatus rs : readStatusList) {
        super.delete(rs.getId());
      }
    } finally {
      lock.unlock();
    }
    /*  복합작업 - 넓은 락, 원자성, 일관성을 확실히 보장
    findAll() + delete() 여러 호출을 묶어서 다른 스레드가 중간에 개입하지 못하게 막음
    */
  }

  @Override
  public void deleteById(UUID id) {
    super.delete(id);
  }

  @Override
  public void deleteAll(List<ReadStatus> readStatuses) {
    for (ReadStatus rs : readStatuses) {
      super.delete(rs.getId());
    }
  }
}