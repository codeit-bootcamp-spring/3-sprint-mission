package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository {
    void save(ReadStatus readStatus);
    Optional<ReadStatus> findByChannelIdAndUserId(UUID channelId, UUID userId);
    Optional<ReadStatus> findById(UUID id);
    List<ReadStatus> findAll();
    List<ReadStatus> findAllByUserId(UUID userId);
    void deleteById(UUID id);
    void deleteByChannelId(UUID channelId);
    void deleteByChannelIdAndUserId(UUID channelId, UUID userId);
}
