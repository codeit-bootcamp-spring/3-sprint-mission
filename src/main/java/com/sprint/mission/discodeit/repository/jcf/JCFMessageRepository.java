package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
@Repository
public class JCFMessageRepository implements MessageRepository {
    private final Map<UUID, Message> messages;

    public JCFMessageRepository() {
        this.messages = new HashMap<>();
    }
    @Override
    public Message save(Message message) {
        this.messages.put(message.getId(), message);
        return message;
    }

    @Override
    public Message findById(UUID id) {
        return this.messages.get(id);
    }
    @Override
    public List<Message> findByChannel(UUID id) {
        return this.messages.values().stream()
                .filter(message -> message.getChannelId().equals(id))
                .collect(Collectors.toList());
    }

    @Override
    public List<Message> findAll() {
        return this.messages.values().stream().toList();
    }

    @Override
    public boolean existsById(UUID id) {
        return this.messages.containsKey(id);
    }

    @Override
    public void delete(UUID id) {
        this.messages.remove(id);

    }
    public void deleteByChannelId(UUID channelId){
        this.messages.values().removeIf(message -> message.getChannelId().equals(channelId));
    }
}
