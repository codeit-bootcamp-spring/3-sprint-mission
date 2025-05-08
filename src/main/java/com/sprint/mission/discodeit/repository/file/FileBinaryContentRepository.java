package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.AbstractFileRepository;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
@Repository
public class FileBinaryContentRepository extends AbstractFileRepository<BinaryContent, UUID> implements BinaryContentRepository {

  public FileBinaryContentRepository(
      @Value("${discodeit.repository.file-directory}") String fileDirectory
  ) {
    super(Paths.get(System.getProperty("user.dir"), fileDirectory, "binaryContent.ser").toString());
  }

  @Override
  public BinaryContent save(BinaryContent content) {
    return super.save(content, content.getId());
  }

  @Override
  public Optional<BinaryContent> find(UUID id) {
    return super.findById(id);
  }

  @Override
  public Optional<BinaryContent> findByUserId(UUID userId) {
    return findAll().stream()
        .filter(content -> content.getUserId().equals(userId))
        .findFirst();
  }

  @Override
  public void delete(UUID id) {
    super.delete(id);
  }
}

