package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class BasicMessageService implements MessageService {
    public final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    public BasicMessageService(MessageRepository messageRepository, ChannelRepository channelRepository,UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Message createMessage(String text, UUID channelId, UUID userId){
        Message message = new Message(text,channelId, userId);
        messageRepository.save(message);
        return message;
    }

    @Override
    public Optional<Message> readMessage(UUID id)
    {
        return Optional.ofNullable(messageRepository.load().get(id));
    }

    @Override
    public Map<UUID, Message> readMessages()
    {
        return messageRepository.load();
    }

    @Override
    public Message updateMessage(UUID id, String text)
    {
        Message message = messageRepository.load().get(id);
        message.updateText(text);
        messageRepository.save(message);
        return message;
    }

    @Override
    public Message deleteMessage(UUID id)
    {
        messageRepository.deleteMessage(id);
        Message message = messageRepository.load().get(id);
        messageRepository.save(message);
        return message;
    }


}
