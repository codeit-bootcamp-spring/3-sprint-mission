package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
public interface ReadStatusRepository {
  ReadStatus save(ReadStatus readStatus);

  Optional<ReadStatus> findById(UUID id);
  Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId);
  List<ReadStatus> findAllByUserId(UUID userId);
  List<ReadStatus> findByChannelId(UUID channelId);

  void updateLastReadAt(UUID id, Instant newReadAt);

  void deleteByUserIdAndChannelId(UUID userId, UUID channelId);
  void deleteById(UUID id);
  void deleteAll(List<ReadStatus> readStatuses);
}
