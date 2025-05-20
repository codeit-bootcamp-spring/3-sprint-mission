package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.UUID;

public interface ReadStatusRepository {
    void save(ReadStatus readStatus);
    ReadStatus loadById(UUID id);
    List<ReadStatus> loadAllByUserId(UUID id);
    void deleteByUserId(UUID userId);
}
