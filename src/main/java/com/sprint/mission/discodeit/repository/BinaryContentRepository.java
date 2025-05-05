package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.entity.BinaryContent;

import java.util.List;
import java.util.UUID;

public interface BinaryContentRepository {
    void save(BinaryContent userProfileImage);
    BinaryContent loadByUserId(UUID userId);
    BinaryContent loadById(UUID userId);
    List<BinaryContent> loadAll();
    void delete(UUID id, UUID userId);
}
