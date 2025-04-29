package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> data; //database
    private final UserService userService;
    private final ChannelService channelService;

    public JCFMessageService(UserService userService, ChannelService channelService) {
        this.data = new HashMap<>();
        this.userService = userService;
        this.channelService = channelService;
    }


    @Override
    public Message create(Message message) {

        try {
            userService.find(message.getUserId());
            Channel channel = channelService.find(message.getChannelId());

            this.data.put(message.getId(), message);

            channelService.addMessageToChannel(channel.getId(), message.getId());

            return message;
        } catch (NoSuchElementException e) {
            throw e;
        }

    }

    @Override
    public Message find(UUID messageId) {
        Message messageNullable = this.data.get(messageId);

        return Optional.ofNullable(messageNullable).orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(this.data.values());
    }

    @Override
    public Message update(UUID messageId, String newContent) {
        Message messageNullable = this.data.get(messageId);
        Message message = Optional.ofNullable(messageNullable).orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));
        message.update(newContent);

        return message;
    }

    @Override
    public void delete(UUID messageId) {
        if (!this.data.containsKey(messageId)) {
            throw new NoSuchElementException("Message with id " + messageId + " not found");
        }
        this.data.remove(messageId);
    }

    //해당 채널의 메세지들을 다 읽음
    @Override
    public List<Message> findMessagesByChannel(UUID channelId) {

        try {
            Channel channel = this.channelService.find(channelId);
            List<UUID> messageIds = channel.getMessages();

            List<Message> messages = new ArrayList<>();
            messageIds.forEach((messageId) -> {
                messages.add(this.data.get(messageId));
            });

            return messages;

        } catch (NoSuchElementException e) {
            throw e;
        }

    }

}
