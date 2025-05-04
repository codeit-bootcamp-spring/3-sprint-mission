package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.common.exception.FileException;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.storage.FileStorage;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileBinaryContentRepository implements BinaryContentRepository {

  private final FileStorage fileStorage;

  private FileBinaryContentRepository(FileStorage fileStorage) {
    this.fileStorage = fileStorage;
  }

  public static FileBinaryContentRepository create(FileStorage fileStorage) {
    return new FileBinaryContentRepository(fileStorage);
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
  public Optional<BinaryContent> findByUserId(UUID userId) {
    return findAll().stream()
        .filter(content -> content.getUserId() != null && content.getUserId().equals(userId))
        .findFirst();
  }

  @Override
  public List<BinaryContent> findAllByMessageId(UUID messageId) {
    return findAll().stream()
        .filter(
            content -> content.getMessageId() != null && content.getMessageId().equals(messageId))
        .toList();
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
  public void delete(UUID id) {
    try {
      fileStorage.deleteObject(id);
    } catch (FileException e) {
      throw new RuntimeException("컨텐트 삭제 실패: " + e.getMessage(), e);
    }
  }

  @Override
  public void deleteAllByMessageId(UUID messageId) {
    List<BinaryContent> contentsToDelete = findAllByMessageId(messageId);
    for (BinaryContent content : contentsToDelete) {
      try {
        fileStorage.deleteObject(content.getId());
      } catch (FileException e) {
        throw new RuntimeException("메시지 ID로 컨텐트 삭제 실패: " + e.getMessage(), e);
      }
    }
  }

  private List<BinaryContent> findAll() {
    return fileStorage.readAll().stream()
        .map(obj -> (BinaryContent) obj)
        .toList();
  }
}
