package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.Optional;
import java.util.UUID;

public interface BinaryContentRepository {
  BinaryContent save(BinaryContent content);
  Optional<BinaryContent> find(UUID id);
  void delete(UUID id);
}