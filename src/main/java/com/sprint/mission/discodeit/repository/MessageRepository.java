package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageRepository {
    void save(Message msg);
    Message loadById(UUID id);
    List<Message> loadAll();
    List<Message> loadByChannelId(UUID channelId);
    void update(UUID id, String content);
    void deleteById(UUID id);
    void deleteByChannelId(UUID channelId);
}
