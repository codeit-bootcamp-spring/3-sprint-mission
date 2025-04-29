package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BinaryContentRepository {
    public BinaryContent save(BinaryContent BinaryContent);

    public Optional<BinaryContent> findById(UUID BinaryContentId);

    public List<BinaryContent> findAll();

    // QUESTION. 이게 필요한가?
    public boolean existsById(UUID BinaryContentId);

    public void deleteById(UUID BinaryContentId);
}
