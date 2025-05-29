package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BinaryContentRepository extends JpaRepository<BinaryContent, UUID> {

  public Optional<BinaryContent> findById(UUID id);

  public List<BinaryContent> findAllByIdIn(List<UUID> ids);

  boolean existsById(UUID id);

  void deleteById(UUID id);

}
