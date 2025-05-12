package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
public class JCFMessageRepository implements MessageRepository {

  private final List<Message> messages = new CopyOnWriteArrayList<>();

  @Override
  public void insert(Message message) {
    if (messages.stream().anyMatch(m -> m.getId().equals(message.getId()))) {
      throw new IllegalArgumentException("이미 존재하는 메시지입니다. [ID: " + message.getId() + "]");
    }
    messages.add(message);
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
  public List<Message> findAllByChannelId(UUID channelId) {
    return messages.stream()
        .filter(message -> message.getChannelId().equals(channelId))
        .toList();
  }

  @Override
  public Message save(Message message) {
    messages.removeIf(m -> m.getId().equals(message.getId()));  // 기존 메시지 제거
    messages.add(message);  // 메시지 추가
    return message;
  }

  @Override
  public void update(Message message) {
    boolean exists = messages.stream().anyMatch(m -> m.getId().equals(message.getId()));
    if (!exists) {
      throw new IllegalArgumentException("존재하지 않는 메시지입니다. [ID: " + message.getId() + "]");
    }
    save(message);  // update는 save로 처리
  }

  @Override
  public void delete(UUID id) {
    boolean removed = messages.removeIf(m -> m.getId().equals(id));
    if (!removed) {
      throw new IllegalArgumentException("메시지를 찾을 수 없습니다. [ID: " + id + "]");
    }
  }
}
