package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;


@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
public class JCFMessageRepository implements MessageRepository {
    private final Map<UUID, Message> data;

    public JCFMessageRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public Message save(Message message) {
        this.data.put(message.getId(), message);
        return message;
    }

    @Override
    public Optional<Message> findById(UUID messageId) {
        return Optional.ofNullable(this.data.get(messageId));
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(this.data.values());
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        return this.findAll().stream().filter((message) -> message.getChannelId().equals(channelId)).toList();
    }

    @Override
    public boolean existsById(UUID messageId) {
        return this.data.containsKey(messageId);
    }

    @Override
    public void deleteById(UUID messageId) {
        this.data.remove(messageId);
    }
}
