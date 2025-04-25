package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository {
    public ReadStatus save(ReadStatus ReadStatus);

    public Optional<ReadStatus> findById(UUID ReadStatusId);

    public Optional<ReadStatus> findByChannelId(UUID channelId);

    public List<ReadStatus> findAll();

    public boolean existsById(UUID ReadStatusId);

    public void deleteById(UUID ReadStatusId);
}
