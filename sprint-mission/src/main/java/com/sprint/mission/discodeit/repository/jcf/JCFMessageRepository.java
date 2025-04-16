package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class JCFMessageRepository implements MessageRepository {
    private final Map<UUID, Message> messages;

    public JCFMessageRepository() {
        this.messages = new HashMap<>();
    }

    @Override
    public void save(Message message) {
        messages.put(message.getId(),message);
    }

    @Override
    public Map<UUID, Message> load() {
        return messages;
    }


    @Override
    public void deleteMessage(UUID id){
        messages.remove(id);
    }
}
