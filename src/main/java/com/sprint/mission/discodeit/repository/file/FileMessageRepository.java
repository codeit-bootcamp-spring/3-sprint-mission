package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileMessageRepository extends AbstractFileRepository<UUID, List<Message>> implements MessageRepository {

    public FileMessageRepository(@Value("${discodeit.repository.file-directory}") String filePath) {
        super(filePath, "/message.ser");
    }

    @Override
    public Message createMessage(Message message, UUID channelId) {
        Map<UUID, List<Message>> messages = loadFromFile();
        messages.get(channelId).add(message);
        saveToFile(messages);
        return message;
    }

    @Override
    public List<Message> getChannelMessages(UUID channelId) {
        return loadFromFile().getOrDefault(channelId, List.of());
    }

    @Override
    public boolean updateMessage(Message message, UUID channelId) {
        Map<UUID, List<Message>> messages = loadFromFile();
        List<Message> channelMessages = messages.get(channelId);
        if (channelMessages == null) return false;

        return channelMessages.stream()
                .filter(msg -> msg.getId().equals(message.getId()))
                .findFirst()
                .map(msg -> {
                    msg.setContent(message.getContent());
                    saveToFile(messages);
                    return true;
                })
                .orElse(false);
    }

    @Override
    public boolean deleteMessage(Message message, UUID channelId) {
        Map<UUID, List<Message>> messages = loadFromFile();
        List<Message> channelMessages = messages.get(channelId);
        if (channelMessages == null) return false;
        channelMessages.remove(message);
        saveToFile(messages);
        return true;
    }
}
