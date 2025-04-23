package com.sprint.mission.discodeit.service.jcf;

import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

public class JCFMessageService implements MessageService {
    private static volatile JCFMessageService instance;
    private final MessageRepository messageRepository;
    private final UserService userService;
    private final ChannelService channelService;

    private JCFMessageService(UserService userService, ChannelService channelService, MessageRepository messageRepository) {
        this.userService = userService;
        this.channelService = channelService;
        this.messageRepository = messageRepository;
    }

    public static JCFMessageService getInstance(UserService userService, ChannelService channelService, MessageRepository messageRepository) {
        JCFMessageService result = instance;
        if (result == null) {
            synchronized (JCFMessageService.class) {
                result = instance;
                if (result == null) {
                    result = new JCFMessageService(userService, channelService, messageRepository);
                    instance = result;
                }
            }
        }
        return result;
    }

    // ... 나머지 메서드들은 동일

    @Override
    public Message createMessage(String content, UUID authorId, UUID channelId) {
        if (userService.getUserById(authorId) == null) {
            throw new IllegalArgumentException("존재하지 않는 작성자 ID입니다.");
        }
        Channel channel = channelService.getChannelById(channelId);
        if (channel == null) {
            throw new IllegalArgumentException("존재하지 않는 채널 ID입니다.");
        }
        if (!channel.isParticipant(authorId)) {
            throw new IllegalStateException("채널 참가자만 메시지를 작성할 수 있습니다.");
        }
        Message message = new Message(content, authorId, channelId);
        return messageRepository.save(message);
    }

    @Override
    public Message getMessageById(UUID messageId) {
        return messageRepository.findById(messageId);
    }

    @Override
    public List<Message> getMessagesByChannel(UUID channelId) {
        return messageRepository.findByChannelId(channelId);
    }

    @Override
    public List<Message> getMessagesByAuthor(UUID authorId) {
        return messageRepository.findByAuthorId(authorId);
    }

    @Override
    public void updateMessage(UUID messageId, String updatedContent) {
        Message message = messageRepository.findById(messageId);
        if (message != null) {
            message.updateContent(updatedContent);
            messageRepository.save(message);
        } else {
            throw new IllegalArgumentException("존재하지 않는 메시지 ID입니다.");
        }
    }

    @Override
    public void deleteMessage(UUID messageId) {
        Message message = messageRepository.findById(messageId);
        if (message == null) {
            throw new IllegalArgumentException("존재하지 않는 메시지 ID입니다.");
        }
        messageRepository.deleteById(messageId);
    }
}
