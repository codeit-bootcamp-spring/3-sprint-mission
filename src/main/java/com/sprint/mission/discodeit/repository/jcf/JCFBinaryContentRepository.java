package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class JCFBinaryContentRepository implements BinaryContentRepository {

  private final Map<UUID, BinaryContent> binaryContentMap = new ConcurrentHashMap<>();

  @Override
  public void insert(BinaryContent binaryContent) {
    if (binaryContentMap.containsKey(binaryContent.getId())) {
      throw new IllegalArgumentException(
          "이미 존재하는 바이너리 컨텐트입니다. [ID: " + binaryContent.getId() + "]");
    }
    binaryContentMap.put(binaryContent.getId(), binaryContent);
  }

  @Override
  public Optional<BinaryContent> findById(UUID id) {
    return Optional.ofNullable(binaryContentMap.get(id));
  }

  @Override
  public List<BinaryContent> findAllByMessageId(UUID messageId) {
    return binaryContentMap.values().stream()
        .filter(content -> Objects.equals(content.getMessageId(), messageId))
        .toList();
  }

  @Override
  public Optional<BinaryContent> findByUserId(UUID userId) {
    return binaryContentMap.values().stream()
        .filter(content -> Objects.equals(content.getUserId(), userId))
        .findFirst();
  }

  @Override
  public BinaryContent save(BinaryContent binaryContent) {
    binaryContentMap.put(binaryContent.getId(), binaryContent);
    return binaryContent;
  }

  @Override
  public void update(BinaryContent binaryContent) {
    if (!binaryContentMap.containsKey(binaryContent.getId())) {
      throw new IllegalArgumentException(
          "존재하지 않는 바이너리 컨텐트입니다. [ID: " + binaryContent.getId() + "]");
    }
    binaryContentMap.put(binaryContent.getId(), binaryContent);
  }

  @Override
  public void delete(UUID id) {
    if (!binaryContentMap.containsKey(id)) {
      throw new IllegalArgumentException("바이너리 컨텐트를 찾을 수 없습니다. [ID: " + id + "]");
    }
    binaryContentMap.remove(id);
  }

  @Override
  public void deleteAllByMessageId(UUID messageId) {
    binaryContentMap.values()
        .removeIf(content -> Objects.equals(content.getMessageId(), messageId));
  }
}
