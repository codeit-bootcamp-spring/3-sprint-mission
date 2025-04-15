package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entitiy.Channel;
import com.sprint.mission.discodeit.entitiy.Message;
import com.sprint.mission.discodeit.entitiy.User;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    public void create(Message message, User user, Channel channel);
    public void readAll();
    public void readById(UUID id);
    public void update(UUID id, Message message);
    public void delete(Message message);

}
