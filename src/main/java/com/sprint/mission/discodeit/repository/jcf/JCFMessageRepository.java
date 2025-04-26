package com.sprint.mission.discodeit.repository.jcf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

public class JCFMessageRepository implements MessageRepository {
    private static JCFMessageRepository instance;
    private final Map<UUID, Message> data = new HashMap<>();

    private JCFMessageRepository() {}

    public static JCFMessageRepository getInstance() {
        if (instance == null) {
            instance = new JCFMessageRepository();
        }
        return instance;
    }

    // 테스트용 메서드
    public static void clearInstance() {
        if (instance != null) {
            instance.clearData();
            instance = null;
        }
    }

    public void clearData() {
        data.clear();
    }

    @Override
    public Message save(Message message) {
        data.put(message.getMessageId(), message);
        return message;
    }

    @Override
    public Message findById(UUID messageId) {
        return data.get(messageId);
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public List<Message> findByChannelId(UUID channelId) {
        return data.values().stream()
                .filter(m -> m.getChannelId().equals(channelId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Message> findByAuthorId(UUID authorId) {
        return data.values().stream()
                .filter(m -> m.getAuthorId().equals(authorId))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID messageId) {
        data.remove(messageId);
    }
}
