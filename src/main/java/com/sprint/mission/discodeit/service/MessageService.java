package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message create(UUID userId, UUID channelId, String content);
    Message findById(UUID id);
    List<Message> findAll();
    void update(UUID id, String newContent);
    void delete(UUID id);
}
