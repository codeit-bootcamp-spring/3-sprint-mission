package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.*;

public class JCFMessageRepository implements MessageRepository {
    private final Map<UUID, Message> data = new HashMap<>();

    public Message save(Message message) {
        return data.put(message.getId(), message);
    }

    public Optional<Message> findById(UUID messageId) {
        return Optional.ofNullable(data.get(messageId));
    }

    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    public void deleteById(UUID messageId) {
        data.remove(messageId);
    }
}
