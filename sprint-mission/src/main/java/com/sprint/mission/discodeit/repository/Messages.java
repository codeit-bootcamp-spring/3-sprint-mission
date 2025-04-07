package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Messages {
    private final Map<UUID, Message> messages = new HashMap<>();

    public Message add(UUID id, Message message) {
        messages.put(id, message);
        return message;
    }

    public Message get(UUID id) {
        return messages.get(id);
    }

    public Map<UUID, Message> readAll() {
        return messages;
    }
    public Message remove(UUID id) {
        return messages.remove(id);
    }

    public Message update(UUID id, String text) {
        Message message = messages.get(id);
        message.updateText(text);
        return message;
    }



}
