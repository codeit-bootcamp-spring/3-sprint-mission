package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf")
public class JCFMessageRepository implements MessageRepository {

    private final Map<UUID, List<Message>> messages;

    public JCFMessageRepository() {
        this.messages = new HashMap<>();
    }

    public Map<UUID, List<Message>> getMessages() {
        return messages;
    }

    @Override
    public Message createMessage(Message message, UUID channelId) {
        List<Message> messages = this.messages.get(channelId);
        messages.add(message);
        this.messages.put(channelId, messages);
        return message;
    }

    @Override
    public List<Message> getChannelMessages(UUID channelId) {
        return messages.get(channelId) == null ? List.of() : messages.get(channelId);
    }

    @Override
    public boolean updateMessage(Message message, UUID channelId) {
        List<Message> messages = this.messages.get(channelId);
        if (messages == null) {
            return false;
        }

        for (Message msg : messages) {
            if (msg.getId().equals(message.getId())) {
                if (msg.getSender().equals(message.getSender())) {
                    msg.setContent(message.getContent());
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean deleteMessage(Message message, UUID channelId) {
        List<Message> messages = this.messages.get(channelId);

        for (Message msg : messages) {
            if (msg.getId().equals(message.getId())) {
                if (msg.getSender().equals(message.getSender())) {
                    messages.remove(message);
                    return true;
                }
            }
        }
        return false;
    }
}
