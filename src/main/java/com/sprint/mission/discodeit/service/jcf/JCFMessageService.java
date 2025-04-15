package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
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

    public Optional<Message> getMessage(UUID messageId) {
        return Optional.ofNullable(data.get(messageId));
    }

    public List<Message> getAllMessages() {
        return new ArrayList<>(data.values());
    }

    public void setChannelService(JCFChannelService channelService) {
        this.channelService = channelService;
    }

    // update 실패 시 피드백 출력
    public void updateMessage(UUID messageId, String message) {
        getMessage(messageId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메시지"))
                .updateMsgContent(message);
    }

    public void deleteMessage(UUID messageId) {
        data.remove(messageId);
    }

    public Message createMessageCheck(String msgContent, UUID senderId, UUID channelId) {
        userService.getUser(senderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저"));

        Channel channel = channelService.getChannel(channelId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널"));

        if(!channel.getUserIds().contains(senderId)) {
            throw new SecurityException("채널에 속해있지 않은 유저"); // 권한이 없는 경우이므로 SecurityException으로 수정
        }

        Message message = new Message(msgContent, senderId, channelId);
        Message created = createMessage(message);
        channelService.addMessageId(channelId, created.getId());
        return created;
    }
}
