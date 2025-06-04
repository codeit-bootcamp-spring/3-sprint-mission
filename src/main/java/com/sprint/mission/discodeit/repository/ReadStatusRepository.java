package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository {
    ReadStatus save(ReadStatus readStatus);
    Optional<ReadStatus> loadById(UUID id);
    List<ReadStatus> loadAllByUserId(UUID id);
    List<ReadStatus> loadAllByChannelId(UUID channelId);
    void deleteByUserId(UUID userId);
    void deleteByChannelId(UUID channelId);
}
