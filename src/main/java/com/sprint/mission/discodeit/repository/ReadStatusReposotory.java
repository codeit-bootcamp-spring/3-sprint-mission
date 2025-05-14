package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.UUID;

@Repository
public interface ReadStatusReposotory {
    ReadStatus save(ReadStatus readStatus);
    ReadStatus findById(UUID id);
    void deleteById(UUID id);
    boolean existsById(UUID id);
    void refresh(UUID id, Instant newRecentReadAt);
}
