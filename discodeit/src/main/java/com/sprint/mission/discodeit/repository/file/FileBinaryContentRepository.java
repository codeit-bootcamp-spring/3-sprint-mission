package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.util.FileioUtil;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@ConditionalOnProperty(value = "repository.type", havingValue = "file")
@Repository
public class FileBinaryContentRepository implements BinaryContentRepository {

  private Map<String, BinaryContent> binaryContentData;
  private final Path path;

  public FileBinaryContentRepository(
      @Value("${spring-doc.repository.binary-content-file-path}") Path path) {
    this.path = path;
    if (!Files.exists(this.path)) {
      try {
        Files.createFile(this.path);
        FileioUtil.save(this.path, new HashMap<>());
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    FileioUtil.init(this.path);
    this.binaryContentData = FileioUtil.load(this.path, BinaryContent.class);
  }


  @Override
  public BinaryContent save(BinaryContent binaryContent) {
    binaryContentData.put(binaryContent.getId().toString(), binaryContent);
    return binaryContent;
  }

  @Override
  public Optional<BinaryContent> findById(UUID id) {
    if (!existsById(id)) {
      throw new NoSuchElementException("BinaryContent not found with id " + id);
    }
    return Optional.ofNullable(binaryContentData.get(id.toString()));
  }

  @Override
  public List<BinaryContent> findAllByIdIn(List<UUID> binaryContentIds) {
    return binaryContentData.values()
        .stream()
        .filter(binaryContent -> binaryContentIds.contains(binaryContent.getId()))
        .toList();
  }

  @Override
  public boolean existsById(UUID id) {
    return binaryContentData.containsKey(id.toString());
  }

  @Override
  public void deleteById(UUID id) {
    if (!existsById(id)) {
      throw new NoSuchElementException("BinaryContent not found with id " + id);
    }
    binaryContentData.remove(id.toString());
    FileioUtil.save(this.path, binaryContentData);
  }
}
