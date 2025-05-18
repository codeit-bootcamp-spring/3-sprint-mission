package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository {

    ReadStatus save(ReadStatus readStatus);
    List<ReadStatus> findAll();
    List<ReadStatus> findAllByChannelId(UUID channelId);
    List<ReadStatus> findAllByUserId(UUID userId);
    Optional<ReadStatus> findById(UUID id);
    boolean existsById(UUID id);
//    boolean existsByUserId(UUID userId);
    void deleteById(UUID id);
    void deleteAllByChannelId(UUID channelId);
}
