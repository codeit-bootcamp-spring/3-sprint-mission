package com.sprint.mission.discodeit.service.jcf.integration;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.UUID;

public class MessageIntegration {
    private final MessageService messageService;
    private final UserService userService;
    private final ChannelService channelService;

    public MessageIntegration(MessageService messageService, UserService userService, ChannelService channelService) {
        this.messageService = messageService;
        this.userService = userService;
        this.channelService = channelService;
    }

    public Message createMessage(String msgContent, UUID senderId, UUID channelId) {
        userService.getUser(senderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저"));

        Channel channel = channelService.getChannel(channelId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널"));

        if(!channel.getUserIds().contains(senderId)) {
            throw new SecurityException("채널에 속해있지 않은 유저"); // 권한이 없는 경우이므로 SecurityException으로 수정
        }

        Message message = new Message(msgContent, senderId, channelId);
        Message created = messageService.createMessage(message);
        channel.addMessage(created.getId());
        return created;
    }

    public void deleteMessage(UUID messageId) {
        Message message = messageService.getMessage(messageId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메시지"));

        channelService.getChannel(message.getChannelId()).ifPresent(channel -> channel.getMessageIds().remove(messageId));
        messageService.deleteMessage(messageId);
    }
}
