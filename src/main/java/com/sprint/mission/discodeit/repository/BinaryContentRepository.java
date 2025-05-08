package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.Optional;
import java.util.UUID;

public interface BinaryContentRepository {
  BinaryContent save(BinaryContent content);
  Optional<BinaryContent> find(UUID id);
  Optional<BinaryContent> findByUserId(UUID userId);
  void delete(UUID id);
}