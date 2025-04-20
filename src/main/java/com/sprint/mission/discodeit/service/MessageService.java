package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    Message create(Message message);

    Message getById(UUID id);

    List<Message> getAll();

    Message update(Message message);

    void delete(UUID id);
}
