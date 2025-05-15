package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {

    public Message save(Message message);

    public Optional<Message> findById(UUID messageId);

    public List<Message> findAll();

    public List<Message> findAllByChannelId(UUID channelId);

    public boolean existsById(UUID messageId);

    public void deleteById(UUID messageId);

    public void deleteAllByChannelId(UUID channelId);
}
