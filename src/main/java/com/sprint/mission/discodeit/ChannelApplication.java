package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.UUID;

public class ChannelApplication {
    private final ChannelService channelService;
    private final UserService userService;
    private final MessageService messageService;

    public ChannelApplication(ChannelService channelService, UserService userService, MessageService messageService) {
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
        channelService.addUserId(channelId, userId);
    }

    public void addMessageToChannel(UUID channelId, UUID messageId) {
        Channel channel = channelService.getChannel(channelId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널"));

        Message message = messageService.getMessage(messageId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메시지"));

        if (!message.getChannelId().equals(channelId)) {
            throw new IllegalStateException("이 채널에 속하지 않은 메시지입니다");
        }

        channelService.addMessageId(channelId, messageId);
    }

    public void deleteChannel(UUID channelId) {
        Channel channel = channelService.getChannel(channelId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널"));

        for (UUID messageId : channel.getMessageIds()) {
            messageService.deleteMessage(messageId);
        }

        channelService.deleteChannel(channelId);
    }
}
