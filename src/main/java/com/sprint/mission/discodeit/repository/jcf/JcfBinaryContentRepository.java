package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
@Repository
public class JcfBinaryContentRepository implements BinaryContentRepository {

  private final Map<UUID, BinaryContent> store = new ConcurrentHashMap<>();

  @Override
  public BinaryContent save(BinaryContent content) {
    store.put(content.getId(), content);
    return content;
  }

  @Override
  public Optional<BinaryContent> find(UUID id) {
    return Optional.ofNullable(store.get(id));
  }

  @Override
  public Optional<BinaryContent> findByUserId(UUID userId) {
    return store.values().stream()
        .filter(content -> content.getUserId().equals(userId))
        .findFirst();
  }

  @Override
  public void delete(UUID id) {
    store.remove(id); // 삭제
  }
}


