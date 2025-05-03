package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.entity.ReadStatus;

import java.util.UUID;

public interface ReadStatusRepository {
    void save(ReadStatus readStatus);
    void deleteByUserId(UUID userId);
}
