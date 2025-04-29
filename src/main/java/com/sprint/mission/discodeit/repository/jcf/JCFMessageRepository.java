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
    public Message save(Message message) {
        data.put(message.getId(), message);

        return message;
    }

    @Override
    public Optional<Message> findById(UUID messageId) {
        Optional<Message> foundMessage = data.entrySet().stream()
                .filter(entry -> entry.getKey().equals(messageId))
                .map(Map.Entry::getValue)
                .findFirst();

        return foundMessage;
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public List<Message> findMessagesByChannelId(UUID channelId) {
        return data.values().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .toList();
    }

    @Override
    public List<Message> findMessagesByUserId(UUID userId) {
        return data.values().stream()
                .filter(message -> message.getSenderId().equals(userId))
                .toList();
    }

    @Override
    public List<Message> findMessageByContainingWord(String word) {
        return data.values().stream()
                .filter(message -> message.getContent().contains(word))
                .toList();
    }

    @Override
    public void deleteById(UUID messageId) {
        data.remove(messageId);
    }
}
