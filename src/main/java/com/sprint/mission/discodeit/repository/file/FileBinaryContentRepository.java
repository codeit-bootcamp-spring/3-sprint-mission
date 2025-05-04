package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class FileBinaryContentRepository implements BinaryContentRepository {

  private final Map<UUID, BinaryContent> store = new HashMap<>();

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
  public void delete(UUID id) {
    store.remove(id); // 삭제
  }
}


