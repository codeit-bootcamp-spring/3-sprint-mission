package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JCFMessageRepository implements MessageRepository {
    private final Map<UUID, Message> messages = new HashMap<>();

    @Override
    public void save(Message message) {
        messages.put(message.getId(),message);
    }

    @Override
    public Map<UUID, Message> readMessages() {
        return messages;
    }

    @Override
    public Message readMessage(UUID id){
        return messages.get(id);
    }

    @Override
    public void deleteMessage(UUID id){
        messages.remove(id);
    }
}
