package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.*;

public class JCFMessageRepository implements MessageRepository {
    private final Map<UUID, Message> data;

    public JCFMessageRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public Message write(Message message) {
        this.data.put(message.getId(), message);
        return message;
    }

    @Override
    public Message read(UUID messageId) {
        return this.data.get(messageId);
    }

    @Override
    public List<Message> readAll() {
        return new ArrayList<>(this.data.values());
    }

    @Override
    public void delete(UUID messageId) {
        this.data.remove(messageId);
    }
}
