package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.*;

public class JCFMessageRepository implements MessageRepository {
    private final Map<UUID, Message> messageMap = new HashMap<>();

    @Override
    public void save(Message message) {
        messageMap.put(message.getMessageId(), message);
    }

    @Override
    public void saveAll(List<Message> messages) {
        messageMap.clear();
        for (Message message : messages) {
            messageMap.put(message.getMessageId(), message);
        }
    }

    @Override
    public List<Message> loadAll() {
        return new ArrayList<>(messageMap.values());
    }

    @Override
    public Message loadById(UUID id) {
        return messageMap.get(id);
    }

    @Override
    public List<Message> loadByType(String type) {
        List<Message> result = new ArrayList<>();
        for (Message message : messageMap.values()) {
            if (message.getMessageType().equals(type)) {
                result.add(message);
            }
        }
        return result;
    }
}
