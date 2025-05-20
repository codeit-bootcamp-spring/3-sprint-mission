package com.sprint.mission.discodeit.repository.jcf;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

public class JCFMessageRepository implements MessageRepository {

    private static volatile JCFMessageRepository instance;
    private final Map<UUID, Message> messages = new ConcurrentHashMap<>();

    private JCFMessageRepository() {}

    public static JCFMessageRepository getInstance() {
        JCFMessageRepository result = instance;
        if (result == null) {
            synchronized (JCFMessageRepository.class) {
                result = instance;
                if (result == null) {
                    instance = result = new JCFMessageRepository();
                }
            }
        }
        return result;
    }

    // 테스트용 메서드
    public static void clearInstance() {
        if (instance != null) {
            instance.clearData();
            instance = null;
        }
    }

    public void clearData() {
        messages.clear();
    }

    @Override
    public Message save(Message message) {
        messages.put(message.getMessageId(), message);
        return message;
    }

    @Override
    public Optional<Message> findById(UUID messageId) {
        return Optional.ofNullable(messages.get(messageId));
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(messages.values());
    }

    @Override
    public List<Message> findByChannelId(UUID channelId) {
        return messages.values().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Message> findByAuthorId(UUID authorId) {
        return messages.values().stream()
                .filter(message -> message.getAuthorId().equals(authorId)) // Assuming Message entity has getUserId
                .collect(Collectors.toList());
    }

    @Override
    public List<Message> findAllByChannelIdOrderByCreatedAtAsc(UUID channelId) {
        return messages.values().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .sorted(Comparator.comparing(Message::getCreatedAt)) // Assuming Message entity has getCreatedAt
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID messageId) {
        messages.remove(messageId);
    }
}
