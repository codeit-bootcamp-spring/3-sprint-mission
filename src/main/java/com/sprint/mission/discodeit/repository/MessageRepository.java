package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {

  public Message save(Message message);

  public Optional<Message> find(UUID id);

  public List<Message> findAllByChannelId(UUID channelId);

  public boolean existsById(UUID id);

  public void deleteById(UUID id);

  public void deleteAllByChannelId(UUID channelId);
}
