package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    public Message create(User user, Channel channel, String content);

    public Message readById(UUID id);

    public List<Message> readByChannelId(UUID id);

    public List<Message> readAll();

    public void update(UUID id, String content);

    public void deleteById(UUID id);
}
