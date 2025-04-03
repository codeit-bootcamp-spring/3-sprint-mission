package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    void create(Message message);

    Message readById(UUID id);

    List<Message> readAll();

    void update(UUID id, Message message);

    void deleteById(UUID id);
}
