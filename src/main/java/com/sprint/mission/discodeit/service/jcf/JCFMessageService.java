package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> data = new HashMap<>();
    private final UserService userService;
    private ChannelService channelService;

    public JCFMessageService(UserService userService, ChannelService channelService) {
        this.userService = userService;
        this.channelService = channelService;
    }

    public Message createMessage(Message message) {
        data.put(message.getId(), message);
        return message;
    }

    public Message getMessage(UUID messageId) {
        return data.get(messageId);
    }

    public List<Message> getAllMessages() {
        return new ArrayList<>(data.values());
    }

    public void setChannelService(JCFChannelService channelService) {
        this.channelService = channelService;
    }

    public void updateMessage(UUID messageId, String message) {
        if (data.containsKey(messageId)) {
            data.get(messageId).updateMsgContent(message);
        }
    }

    public void deleteMessage(UUID messageId) {
        data.remove(messageId);
    }

    public Message createMessageCheck(String msgContent, UUID senderId, UUID channelId) {
        User sender = userService.getUser(senderId);
        if (sender == null) {
            throw new IllegalArgumentException("존재하지 않는 유저");
        }

        Channel channel = channelService.getChannel(channelId);
        if (channel == null) {
            throw new IllegalArgumentException("존재하지 않는 채널");
        }

        if(!channel.getUserIds().contains(senderId)) {
            throw new IllegalStateException("채널에 속해있지 않은 유저");
        }

        Message message = new Message(msgContent, senderId, channelId);
        Message created = createMessage(message);
        channelService.addMessageToChannel(channelId, created.getId());
        return created;
    }
}
