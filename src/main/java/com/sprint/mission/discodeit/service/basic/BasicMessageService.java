package com.sprint.mission.discodeit.service.basic;

import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;

public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    public BasicMessageService(UserRepository userRepository, ChannelRepository channelRepository, MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public Message createMessage(String content, UUID authorId, UUID channelId) {
        User author = userRepository.findById(authorId);
        Channel channel = channelRepository.findById(channelId);
        if (author == null) throw new IllegalArgumentException("존재하지 않는 작성자입니다.");
        if (channel == null) throw new IllegalArgumentException("존재하지 않는 채널입니다.");
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
    public void updateMessage(UUID messageId, String newContent) {
        Message message = messageRepository.findById(messageId);
        if (message == null) throw new IllegalArgumentException("존재하지 않는 메시지입니다.");
        message.updateContent(newContent);
        messageRepository.save(message);
    }

    @Override
    public void deleteMessage(UUID messageId) {
        Message message = messageRepository.findById(messageId);
        if (message == null) throw new IllegalArgumentException("존재하지 않는 메시지입니다.");
        messageRepository.deleteById(messageId);
    }
}
