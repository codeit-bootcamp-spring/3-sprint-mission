package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.dto.entity.Channel;
import com.sprint.mission.discodeit.dto.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

public class FileMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final UserService userService;
    private final ChannelService channelService;

    public FileMessageService(UserService userService, ChannelService channelService) {
        messageRepository = new FileMessageRepository();
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message createMessage(UUID userId, UUID channelId, String content) {
        try {
            Channel ch = channelService.getChannel(channelId);
            if (userService.getUser(userId) == null || ch == null) {
                throw new IllegalArgumentException("[Message] 유효하지 않은 userId 혹은 channelId가 존재합니다. " +
                        "(userId: " + userId + ", channelId: " + channelId + ")");
            }

            if (!ch.isMember(userId)) {
                throw new IllegalAccessException("[Message] 먼저 채널에 접속해주세요.");
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
    public Message getMessage(UUID id) { return messageRepository.loadById(id); }

    @Override
    public List<Message> getMessagesByChannel(UUID channelId) {
        Channel ch;
        try {
            ch = channelService.getChannel(channelId);
        } catch (IllegalArgumentException e) {
            return null;
        }

        return messageRepository.loadByChannel(ch.getId());
    }

    @Override
    public List<Message> getAllMessages() { return messageRepository.loadAll(); }

    @Override
    public void updateMessage(UUID id, String content) {
        try {
            messageRepository.update(id, content);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void deleteMessage(UUID id) {
        try {
            messageRepository.deleteById(id);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }
}
