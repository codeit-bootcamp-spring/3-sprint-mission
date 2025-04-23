package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageRepository {
    List<Message> getMessages(UUID channelId);
    void saveMessages(UUID channelId, List<Message> messages);
}
