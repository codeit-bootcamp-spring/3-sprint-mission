package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;

import java.util.*;

@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;

    @Override
    public Message createMessage(Message message) {
        return messageRepository.save(message);
    }

    @Override
    public Optional<Message> getMessage(UUID messageId) {
        return messageRepository.findById(messageId);
    }

    @Override
    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    @Override
    public void updateMessage(UUID messageId, String msgContent) {
        getMessage(messageId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메시지"))
                .updateMsgContent(msgContent);
        getMessage(messageId).ifPresent(messageRepository::save);
    }

    @Override
    public void deleteMessage(UUID messageId) {
        messageRepository.deleteById(messageId);
    }
}