package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entitiy.Channel;
import com.sprint.mission.discodeit.entitiy.Message;
import com.sprint.mission.discodeit.entitiy.User;

import java.util.UUID;

public interface MessageRepository {
    public void save(Message message, User user, Channel channel);
    public void read();
    public void readById(UUID id);
    public void update(UUID id, Message message);
    public void delete(Message message);
}
