package com.sprint.mission.discodeit.service.jcf.integration;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.UUID;

public class ChannelIntegration {
    private final ChannelService channelService;
    private final UserService userService;
    private final MessageService messageService;

    public ChannelIntegration(ChannelService channelService, UserService userService, MessageService messageService) {
        this.channelService = channelService;
        this.userService = userService;
        this.messageService = messageService;
    }

        public void addUserToChannel(UUID channelId, UUID userId) {
        Channel channel = channelService.getChannel(channelId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널"));

        User user = userService.getUser(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저"));

        user.addChannel(channelId);
        channel.addUser(userId);
    }

    public void deleteChannel(UUID channelId) {
        Channel channel = channelService.getChannel(channelId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널"));

        for (UUID messageId : channel.getMessageIds()) {
            messageService.deleteMessage(messageId);
        }

        for (UUID userId : channel.getUserIds()) {
            userService.getUser(userId).ifPresent(user -> user.getChannelIds().remove(channelId));
        }

        channelService.deleteChannel(channelId);
    }
}
