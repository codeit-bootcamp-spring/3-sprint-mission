package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import java.util.ArrayList;
import java.util.List;

public class JCFMessageRepository implements MessageRepository {

    private final List<Message> messages;

    public JCFMessageRepository() {
        messages = new ArrayList<>();
    }

    @Override
    public void saveMessage(Message message) {
        messages.add(message);
    }

    @Override
    public void updateMessage(Message message) {
        deleteMessage(message.getNumber());
        messages.add(message);
    }

    @Override
    public void deleteMessage(int messageNumber) {
        messages.removeIf(msg -> msg.getNumber() == messageNumber);
    }

    @Override
    public Message findMessage(int messageNumber) {
        return messages.stream()
                .filter(msg -> msg.getNumber() == messageNumber)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Message> findAllMessage() {
        return messages;
    }
}