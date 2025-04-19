package com.sprint.mission.discodeit.Repository.jcf;

import com.sprint.mission.discodeit.Repository.MessageRepository;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public class JCFMessageRepository implements MessageRepository {
    @Override
    public void save(Message msg) {

    }

    @Override
    public Message loadById(UUID id) {
        return null;
    }

    @Override
    public List<Message> loadAll() {
        return List.of();
    }

    @Override
    public List<Message> loadByChannel(UUID channelId) {
        return List.of();
    }

    @Override
    public void update(UUID id, String content) {

    }

    @Override
    public void deleteById(UUID id) {

    }
}
