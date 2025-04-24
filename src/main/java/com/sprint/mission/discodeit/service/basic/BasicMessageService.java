package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

public class BasicMessageService implements MessageService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;

    public BasicMessageService(MessageRepository messageRepository, UserRepository userRepository, ChannelRepository channelRepository) {
        this.messageRepository = messageRepository;
        this.userRepository   = userRepository;
        this.channelRepository = channelRepository;
    }

    @Override
    public Message createMessage(UUID userId, UUID channelId, String content) {
        try {
            Channel ch = channelRepository.loadById(channelId);
            if (userRepository.loadById(userId) == null || ch == null) {
                throw new IllegalArgumentException("[Message] 유효하지 않은 userId 혹은 채널명이 존재합니다. (userId: " + userId + ", channelId: " + channelId);
            }

            if (!ch.isMember(userId)) {
                throw new IllegalAccessException("[Message] 먼저 채널에 접속해주세요. (userId: " + userId + ", channelId: " + channelId);
            }
        } catch (IllegalArgumentException | IllegalAccessException e) {
            System.out.println(e.getMessage());
            return null;
        }

        Message msg = Message.of(userId, channelId, content);
        messageRepository.save(msg);
        return msg;
    }

    @Override
    public Message getMessage(UUID id) {
        return messageRepository.loadById(id);
    }

    @Override
    public List<Message> getMessagesByChannel(UUID channelId) {
        return messageRepository.loadByChannel(channelId);
    }

    @Override
    public List<Message> getAllMessages() {
        return messageRepository.loadAll();
    }

    @Override
    public void updateMessage(UUID id, String content) {
        messageRepository.update(id, content);
    }

    @Override
    public void deleteMessage(UUID id) {
        messageRepository.deleteById(id);
    }
}
