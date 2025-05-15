package com.sprint.mission.discodeit.service.jcf;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
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

    @Override
    public Message createMessage(MessageCreateRequest request) {
        if (userService.getUserById(request.authorId()) == null) {
            throw new IllegalArgumentException("존재하지 않는 작성자 ID입니다.");
        }
        Channel channel = channelService.getChannelById(request.channelId());
        Message message = new Message(request.content(), request.authorId(), request.channelId());
        return messageRepository.save(message);
    }

    @Override
    public Message findMessageById(UUID messageId) {
        Optional<Message> messageOptional = messageRepository.findById(messageId);
        return messageOptional.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메시지 ID입니다."));
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        return messageRepository.findByChannelId(channelId);
    }

    @Override
    public Message updateMessage(UUID messageId, MessageUpdateRequest request) {
        Optional<Message> messageOptional = messageRepository.findById(messageId);
        Message message = messageOptional.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메시지입니다"));

        if (request.newContent() != null && !request.newContent().isEmpty()) {
            message.updateContent(request.newContent());
        }

        return messageRepository.save(message);
    }

    @Override
    public void deleteMessage(UUID messageId) {
        Optional<Message> messageOptional = messageRepository.findById(messageId);
        Message message = messageOptional.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메시지 ID입니다."));
        messageRepository.deleteById(messageId);
    }
}
