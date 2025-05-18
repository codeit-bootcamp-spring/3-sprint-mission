package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.ReadStatus;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BinaryContentRepository {

    BinaryContent save(BinaryContent binaryContent);
    List<BinaryContent> findAll();
    List<BinaryContent> findAllByIdIn(List<UUID> ids);
    Optional<BinaryContent> findById(UUID id);
    boolean existsById(UUID id);
    void deleteById(UUID id);
}
