package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Primary
@Repository
public class JCFMessageRepository implements MessageRepository {

  private final List<Message> messages = new CopyOnWriteArrayList<>();

  @Override
  public Message save(Message message) {
    // 이미 존재하면 갱신, 아니면 추가
    messages.removeIf(m -> m.getId().equals(message.getId()));
    messages.add(message);
    return message;
  }

  @Override
  public Optional<Message> findById(UUID id) {
    return messages.stream().filter(m -> m.getId().equals(id)).findFirst();
  }

  @Override
  public List<Message> findAll() {
    return new ArrayList<>(messages);
  }

  @Override
  public void deleteById(UUID id) {
    messages.removeIf(m -> m.getId().equals(id));
  }
}
