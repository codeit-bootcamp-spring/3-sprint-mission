package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
@Repository
public class JCFMessageRepository implements MessageRepository {

  private final Map<UUID, Message> data;

  public JCFMessageRepository() {
    this.data = new HashMap<>();
  }

  //메시지 생성
  @Override
  public Message save(Message message) {
    data.put(message.getId(), message);
    return message;
  }

  //메시지 조회
  @Override
  public Optional<Message> find(UUID id) {
    return Optional.ofNullable(this.data.get(id));
  }

  @Override
  public List<Message> findAllByChannelId(UUID channelId) {
    return this.data.values().stream().filter(message -> message.getChannelId().equals(channelId))
        .toList();
  }

  @Override
  public boolean existsById(UUID id) {
    return this.data.containsKey(id);
  }

  @Override
  public void deleteById(UUID id) {
    this.data.remove(id);
  }

  @Override
  public void deleteAllByChannelId(UUID channelId) {
    this.findAllByChannelId(channelId)
        .forEach(message -> this.deleteById(message.getId()));
  }
}
