package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data = new HashMap<>();
    private final MessageService messageService;
    private final UserService userService;

    public JCFChannelService(MessageService messageService, UserService userService) {
        this.messageService = messageService;
        this.userService = userService;
    }

    public Channel createChannel(Channel channel) {
        data.put(channel.getId(), channel);
        return channel;
    }

    public Channel getChannel(UUID channelId) {
        return data.get(channelId);
    }

    public List<Channel> getAllChannels() {
        return new ArrayList<>(data.values());
    }

    public void updateChannel(UUID channelId, String channelName) {
        Channel channel = data.get(channelId);
        if (channel != null) {
            channel.updateChannelName(channelName);
        }
    }

    public void deleteChannel(UUID channelId) {
        Channel channel = getChannel(channelId);
        if (channel == null) {
            throw new IllegalArgumentException("존재하지 않는 채널");
        }

        for (UUID messageId : new HashSet<>(channel.getMessageIds())) {
            messageService.deleteMessage(messageId);
        }

        data.remove(channelId);
    }

    public void addUserToChannel(UUID channelId, UUID userId) {
        Channel channel = data.get(channelId);
        if (channel == null) {
            throw new IllegalArgumentException("존재하지 않는 채널");
        }

        User user = userService.getUser(userId);
        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 유저");
        }

        channel.addUser(userId);
        user.addChannel(channelId);
    }

    public void addMessageToChannel(UUID channelId, UUID messageId) {
        Channel channel = data.get(channelId);
        if (channel == null) {
            throw new IllegalArgumentException("존재하지 않는 채널");
        }

        Message message = messageService.getMessage(messageId);
        if(message == null) {
            throw new IllegalArgumentException("존재하지 않는 메시지");
        }

        if (!message.getChannelId().equals(channelId)) {
            throw new IllegalStateException("이 채널에 속하지 않은 메시지입니다");
        }

        channel.addMessage(messageId);
    }
}
