package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository {
    public ReadStatus save(ReadStatus readStatus);

    public Optional<ReadStatus> findById(UUID readStatusId);

    public List<ReadStatus> findAllByUserId(UUID userId);

    public List<ReadStatus> findAllByChannelId(UUID channelId);

    public List<ReadStatus> findAll();

    public boolean existsById(UUID readStatusId);

    public void deleteById(UUID readStatusId);

    public void deleteAllByChannelId(UUID channelId);
}
