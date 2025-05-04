package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.*;

public class JCFMessageRepository implements MessageRepository {
    private final Map<UUID, Message> data = new HashMap<>();

    @Override
    public Message save(Message message) {
        data.put(message.getId(), message);
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void deleteById(UUID id) {
        data.remove(id);
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        List<Message> results = new ArrayList<>();
        for (Message message : data.values()) {
            if (message.getChannelId().equals(channelId)) {
                results.add(message);
            }
        }
        return results;
    }

//    private final Map<UUID, Message> data = new HashMap<>();
//
//    public Message save(Message message) {
//        return data.put(message.getId(), message);
//    }
//
//    public Optional<Message> findById(UUID messageId) {
//        return Optional.ofNullable(data.get(messageId));
//    }
//
//    public List<Message> findAll() {
//        return new ArrayList<>(data.values());
//    }
//
//    public void deleteById(UUID messageId) {
//        data.remove(messageId);
//    }
}
