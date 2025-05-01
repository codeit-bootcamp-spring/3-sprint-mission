package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Primary
@Repository
public class JCFBinaryContentRepository implements BinaryContentRepository {

  private final Map<UUID, BinaryContent> binaryContentMap = new ConcurrentHashMap<>();

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
  public void delete(UUID id) {
    Optional.ofNullable(binaryContentMap.get(id))
        .ifPresent(status -> {
          binaryContentMap.remove(id);
        });
  }

  @Override
  public void deleteAllByMessageId(UUID messageId) {
    binaryContentMap.values().removeIf(content ->
        Objects.equals(content.getMessageId(), messageId)
    );
  }
}
