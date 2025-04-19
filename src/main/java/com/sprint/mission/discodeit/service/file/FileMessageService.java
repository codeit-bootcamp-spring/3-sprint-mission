package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class FileMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final ChannelService channelService;
    private final UserService userService;

    public FileMessageService(ChannelService channelService, UserService userService) {
        this.messageRepository = new FileMessageRepository();
        this.channelService = channelService;
        this.userService = userService;
    }

    @Override
    public Message createMessage(String content, UUID channelId, UUID userId) {
        userService.getUser(userId);
        channelService.getChannel(channelId);
        return messageRepository.save(new Message(userId, channelId, content));
    }

    @Override
    public Message getMessage(UUID id) {
        return messageRepository.findById(id);
    }

    @Override
    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    @Override
    public Message updateMessage(Message message, String newContent) {
        message.updateContent(newContent);
        return messageRepository.save(message);
    }

    @Override
    public Message deleteMessage(Message message) {
        return messageRepository.delete(message);
    }
}
