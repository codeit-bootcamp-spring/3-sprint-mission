package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository {

    public ReadStatus createReadStatus(ReadStatus status);

    public Optional<ReadStatus> findReadStatusById(UUID readStatusId);

    public List<ReadStatus> findAllByUserId(UUID userId);

    public boolean updateReadStatus(ReadStatus readStatus);

    public boolean deleteReadStatus(UUID readStatusId);
}
