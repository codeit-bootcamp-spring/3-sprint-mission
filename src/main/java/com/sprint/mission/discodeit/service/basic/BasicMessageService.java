package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entitiy.Channel;
import com.sprint.mission.discodeit.entitiy.Message;
import com.sprint.mission.discodeit.entitiy.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.UUID;

public class BasicMessageService implements MessageService {

    MessageRepository messageRepository;

    public BasicMessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public void create(Message message, User user, Channel channel) {
        messageRepository.save(message,user,channel);
    }

    @Override
    public void readAll() {
        messageRepository.read();
    }

    @Override
    public void readById(UUID id) {
        messageRepository.readById(id);
    }

    @Override
    public void update(UUID id, Message message) {
        messageRepository.update(id, message);
    }

    @Override
    public void delete(Message message) {
        messageRepository.delete(message);
    }
}
