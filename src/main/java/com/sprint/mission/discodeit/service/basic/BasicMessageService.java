package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.UUID;

public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;

    public BasicMessageService(MessageRepository messageRepository) {
        if (messageRepository == null) {
            throw new NullPointerException("messageRepository is null");
        }
        this.messageRepository = messageRepository;
    }

    @Override
    public void createMessage(Message message) {
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }
        if (message.getMessageContent() == null || message.getMessageContent().isEmpty()) {
            throw new IllegalArgumentException("Message content cannot be empty");
        }

        messageRepository.save(message);
    }

    @Override
    public Message readMessage(UUID id) {
        return messageRepository.loadById(id);
    }

    @Override
    public List<Message> readMessageByType(String messageType) {
        return messageRepository.loadByType(messageType);
    }

    @Override
    public List<Message> readAllMessages() {
        return messageRepository.loadAll();
    }

    @Override
    public Message updateMessage(UUID id, Message message) {
        if (id == null) {
            throw new IllegalArgumentException("Message id cannot be null");
        }
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }
        if (message.getMessageContent() == null || message.getMessageContent().isEmpty()) {
            throw new IllegalArgumentException("Message content cannot be empty");
        }

        messageRepository.save(message);
        return message;
    }

    @Override
    public boolean deleteMessage(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Message id cannot be null");
        }

        List<Message> messages = messageRepository.loadAll();
        boolean removed = messages.removeIf(message -> message.getMessageId().equals(id));
        if (removed) {
            messageRepository.saveAll(messages);
            return true;
        }
        return false;
    }
}
