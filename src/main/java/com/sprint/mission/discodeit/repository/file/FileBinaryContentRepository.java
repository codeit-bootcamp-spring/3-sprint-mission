package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.storage.FileStorage;
import com.sprint.mission.discodeit.repository.storage.FileStorageImpl;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Primary
@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file", matchIfMissing = true)
public class FileBinaryContentRepository implements BinaryContentRepository {

  private final FileStorage fileStorage;

  public FileBinaryContentRepository(
      @Value("${discodeit.repository.file-directory.folder:data}${discodeit.repository.file-directory.binary-content:/binary-content}") String fileDirectory) {
    this.fileStorage = new FileStorageImpl(fileDirectory);
  }

  @Override
  public void insert(BinaryContent binaryContent) {
    Optional<BinaryContent> existing = findById(binaryContent.getId());
    if (existing.isPresent()) {
      throw new IllegalArgumentException("이미 존재하는 컨텐트입니다. [ID: " + binaryContent.getId() + "]");
    }
    fileStorage.saveObject(binaryContent.getId(), binaryContent);
  }

  @Override
  public Optional<BinaryContent> findById(UUID id) {
    try {
      return Optional.of((BinaryContent) fileStorage.readObject(id));
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  @Override
  public BinaryContent save(BinaryContent binaryContent) {
    Optional<BinaryContent> existing = findById(binaryContent.getId());
    if (existing.isPresent()) {
      fileStorage.updateObject(binaryContent.getId(), binaryContent);
    } else {
      fileStorage.saveObject(binaryContent.getId(), binaryContent);
    }
    return binaryContent;
  }

  @Override
  public void update(BinaryContent binaryContent) {
    Optional<BinaryContent> existing = findById(binaryContent.getId());
    if (existing.isEmpty()) {
      throw new IllegalArgumentException("존재하지 않는 컨텐트입니다. [ID: " + binaryContent.getId() + "]");
    }
    fileStorage.updateObject(binaryContent.getId(), binaryContent);
  }

  @Override
  public void delete(UUID binaryContentId) {
    try {
      fileStorage.deleteObject(binaryContentId);
    } catch (RuntimeException e) {
      throw new RuntimeException("컨텐트 삭제 실패: " + e.getMessage(), e);
    }
  }
}
