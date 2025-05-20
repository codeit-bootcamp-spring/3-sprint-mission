package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
@Profile("jcf")
public class JCFMessageRepository implements MessageRepository {
    private final Map<UUID, Message> messages = new HashMap<>();

    @Override
    public void save(Message msg) {
        messages.put(msg.getId(), msg);
    }

    @Override
    public Message loadById(UUID id) {
        return messages.get(id);
    }

    @Override
    public List<Message> loadAll() {
        return messages.values().stream().toList();
    }

    @Override
    public List<Message> loadByChannelId(UUID channelId) {
        return messages.values().stream()
                .filter(m -> m.getChannelId().equals(channelId))
                .toList();
    }

    @Override
    public void update(UUID id, String content) {
        Message msg = loadById(id);
        msg.update(content);
    }

    @Override
    public void deleteById(UUID id) { messages.remove(id); }

    @Override
    public void deleteByChannelId(UUID channelId) {
        loadByChannelId(channelId).stream()
                .map(Message::getId)
                .forEach(this::deleteById);
    }
}
