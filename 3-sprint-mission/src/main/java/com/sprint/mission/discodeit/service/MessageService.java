package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    public void create(Message message);

    public List<Message> readById(UUID id);

    public List<Message> readByChannelId(UUID id);

    public List<Message> readAll();

    public void update(UUID id, String content);

    public void deleteById(UUID id);
}
