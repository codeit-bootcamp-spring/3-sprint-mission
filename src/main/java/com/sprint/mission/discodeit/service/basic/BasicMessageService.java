package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    //
    private final UserService userService;
    private final ChannelService channelService;

    public BasicMessageService(MessageRepository messageRepository, UserService userService, ChannelService channelService) {
        this.messageRepository = messageRepository;
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message create(Message message) {
        return this.messageRepository.save(message);
    }

    @Override
    public Message find(UUID messageId) {
        return this.messageRepository.findById(messageId).orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));
    }

    @Override
    public List<Message> findAll() {
        return this.messageRepository.findAll();
    }

    @Override
    public Message update(UUID messageId, String newContent) {
        Message message = this.find(messageId);
        message.update(newContent);

        return this.create(message);
    }

    @Override
    public void delete(UUID messageId) {
        if (!messageRepository.existsById(messageId)) {
            throw new NoSuchElementException("Message with id " + messageId + " not found");
        }
        this.messageRepository.deleteById(messageId);
    }

    @Override
    public List<Message> findMessagesByChannel(UUID channelId) {
        Channel channel = this.channelService.find(channelId);
        List<UUID> messageIds = channel.getMessages();

        List<Message> messages = new ArrayList<>();
        messageIds.forEach((messageId) -> {
            messages.add(this.find(messageId));
        });

        return messages;
    }
}
