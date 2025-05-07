package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entitiy.Channel;
import com.sprint.mission.discodeit.entitiy.Message;
import com.sprint.mission.discodeit.entitiy.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {
    public Message save(Message message);
    public List<Message> read();
    public List<Message> readByChannelId(UUID id);
    public Optional<Message> readById(UUID id);
    public void update(UUID id, Message message);
    public void delete(UUID messageId);
}
