package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Repository

@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf")
public class JCFMessageRepository implements MessageRepository {
    private final Map<UUID, Message> store = new HashMap<>();

    @Override
    public Message save(Message message) {
        store.put(message.getId(), message);
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        return store.values().stream()
                .filter(msg -> msg.getChannelId().equals(channelId))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAllByChannelId(UUID channelId) {
        store.values().removeIf(msg -> msg.getChannelId().equals(channelId));
    }

    @Override
    public boolean existsById(UUID id) {
        return store.containsKey(id);
    }

    @Override
    public void deleteById(UUID id) {
        store.remove(id);
    }
}