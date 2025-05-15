package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BinaryContentRepository {
    public BinaryContent save(BinaryContent binaryContent);

    public Optional<BinaryContent> findById(UUID binaryContentId);

    public List<BinaryContent> findAll();

    public List<BinaryContent> findAllByIdIn(List<UUID> ids);

    public boolean existsById(UUID binaryContentId);

    public void deleteById(UUID binaryContentId);
}
