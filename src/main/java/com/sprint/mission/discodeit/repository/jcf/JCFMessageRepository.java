package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entitiy.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "JCF",matchIfMissing = true)
public class JCFMessageRepository implements MessageRepository {

    private final CopyOnWriteArrayList<Message> data = new CopyOnWriteArrayList<>();


    @Override
    public Message save(Message message) {
        data.add(message);
        return message;
    }

    @Override
    public List<Message> read() {
        return data;
    }

    @Override
    public List<Message> readByChannelId(UUID channelId) {
        List<Message> channelMessages = data.stream()
                .filter((c)->c.getChannelId().equals(channelId))
                .collect(Collectors.toList());
        return channelMessages;
    }

    @Override
    public Optional<Message> readById(UUID id) {
        Optional<Message> message = data.stream()
                .filter((u)->u.getId().equals(id))
                .findAny();
        return message;
    }

    @Override
    public void update(UUID id, Message message) {
        data.stream()
                .filter((c)->c.getId().equals(id))
                .forEach((c)->{
                    c.setUpdatedAt(Instant.now());
                    c.setText(message.getText());
                    c.setAttachmentIds(message.getAttachmentIds());
                });
    }

    @Override
    public void delete(UUID messageId) {
        data.removeIf(message -> message.getId().equals(messageId));
    }
}

