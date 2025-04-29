package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageRepository {
    void create(Message message);

    Message findById(UUID id);

    List<Message> findAll();

    void updateMessage(UUID id, String newContent);

    void delete(UUID id);
}
