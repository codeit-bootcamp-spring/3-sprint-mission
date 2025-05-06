package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.MessageService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;

    public BasicMessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public Message create(UUID currentUser, UUID currentChannel, String text) {
        Message message = new Message(currentUser, currentChannel, text);
        this.messageRepository.save(message);
        return message;
    }

    @Override
    public List<Message> findAllByChannelId(UUID currentChannel) {

        return this.messageRepository.findAllFromChannel(currentChannel);
    }

    @Override
    public Message find(UUID id) {

        return this.messageRepository.find(id);
    };

    @Override
    public List<Message> findByText(String text) {

        return this.messageRepository.findByText(text);
    }

    @Override
    public void update(UUID id, String text) {
        Message message = this.messageRepository.find(id);
        message.updateText(text);
        this.messageRepository.save(message);
    }

    @Override
    public void delete(UUID id) throws IOException {
        this.messageRepository.delete(id);
    }
}
