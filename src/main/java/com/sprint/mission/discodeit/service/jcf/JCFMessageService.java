package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

public class JCFMessageService implements MessageService {

    private final Map<UUID, Message> messageList;
    private final UserService userService;
    private final ChannelService channelService;

    public JCFMessageService(UserService userService, ChannelService channelService) {
        this.messageList = new LinkedHashMap<>();
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message create(User user, Channel channel, String content) {

        User realUser = userService.readById(user.getId());
        if (realUser == null) {
            System.out.println("User not found!!!");
            throw new IllegalArgumentException("User not found: " + user.getId());
        }

        Channel realChannel = channelService.readById(channel.getId());
        if (realChannel == null) {
            System.out.println("Channel not found!!!");
            throw new IllegalArgumentException("Channel not found: " + channel.getId());
        }

        Message message = new Message(realUser, realChannel, content);
        messageList.put(message.getId(), message);
        channelService.addMessage(channel, message);
        return message;
    }

    @Override
    public Message readById(UUID id) {
        return messageList.get(id);
    }

    @Override
    public List<Message> readByChannelId(UUID id) {
//        return messageList.values()
//                .stream().filter(m -> m.getChannel().getId().equals(id))
//                .collect(Collectors.toList());
        return channelService.readById(id).getMessageList();
    }

    @Override
    public List<Message> readAll() {

        return messageList.values().stream().collect(Collectors.toList());
    }

    @Override
    public void update(UUID id, String content) {

        Message message = messageList.get(id);
        message.update(content);
    }

    @Override
    public void deleteById(UUID id) {
        channelService.deleteMessage(messageList.get(id).getChannel(), messageList.get(id));
        messageList.remove(id);
    }

}
