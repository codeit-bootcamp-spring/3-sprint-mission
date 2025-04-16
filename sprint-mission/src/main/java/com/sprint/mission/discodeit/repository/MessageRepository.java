package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {
    void save(Message message);

    Map<UUID, Message> load();

    void deleteMessage(UUID id);
}
