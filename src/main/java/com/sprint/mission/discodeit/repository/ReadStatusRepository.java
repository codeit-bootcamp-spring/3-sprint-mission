package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ReadStatusRepository {
  ReadStatus save(ReadStatus readStatus);
  ReadStatus find(UUID userId, UUID channelId);
  void updateLastReadAt(UUID userId, UUID channelId, Instant newReadAt);

  List<ReadStatus> findByChannelId(UUID channelId);
  void deleteAll(List<ReadStatus> readStatuses);
}
