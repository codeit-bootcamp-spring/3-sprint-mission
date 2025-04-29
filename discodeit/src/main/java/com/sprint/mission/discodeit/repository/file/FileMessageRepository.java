package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.util.FileioUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@ConditionalOnProperty(name = "discodeit.repository", havingValue = "file")
@Repository
public class FileMessageRepository implements MessageRepository {
    private Map<String, Message> messageData;
    private final Path path;

    public FileMessageRepository(@Qualifier("messageFilePath") Path path) {
        this.path = path;
        if (!Files.exists(this.path)) {
            try {
                Files.createFile(this.path);
                FileioUtil.save(this.path, new HashMap<>());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        FileioUtil.init(this.path);
        this.messageData = FileioUtil.load(this.path, Message.class);
    }


    @Override
    public Message save(Message message) {
        messageData.put(message.getId().toString(), message);
        FileioUtil.save(this.path, messageData);
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        if (!messageData.containsKey(id.toString())) {
            throw new NoSuchElementException("Message not found with id " + id);
        }
        return Optional.ofNullable(messageData.get(id.toString()));
    }


    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        return messageData.values()
                .stream()
                .filter(message -> message.getChannelId().equals(channelId)).toList();
    }

    @Override
    public boolean existsById(UUID id) {
        return messageData.containsKey(id.toString());
    }

    @Override
    public void deleteById(UUID id) {
        messageData.remove(id.toString());
        FileioUtil.save(this.path, messageData);
    }

    @Override
    public void deleteByChannelId(UUID channelId) {
        messageData.values().removeIf(message -> message.getChannelId().equals(channelId));
        FileioUtil.save(this.path, messageData);
    }
}
