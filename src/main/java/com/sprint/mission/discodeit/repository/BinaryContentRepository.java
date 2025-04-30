package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entitiy.BinaryContent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BinaryContentRepository {

    public BinaryContent save(BinaryContent binaryContent);
    public List<BinaryContent> read();
    public Optional<BinaryContent> readById(UUID id);
    public void update(UUID id, BinaryContent binaryContent);
    public void delete(UUID binaryContentId);
}
