package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> data = new HashMap<>();

    public Message createMessage(Message message) {
        data.put(message.getId(), message);
        return message;
    }

    public Message getMessage(UUID messageId) {
        return data.get(messageId);
    }

    public List<Message> getAllMessages() {
        return new ArrayList<>(data.values());
    }

    public void updateMessage(UUID messageId, String message) {
        if (data.containsKey(messageId)) {
            data.get(messageId).updateMsgContent(message);
        }
    }

    public void deleteMessage(UUID messageId) {
        data.remove(messageId);
    }
}
