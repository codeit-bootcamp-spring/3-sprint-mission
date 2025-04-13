package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> data;
    private final ChannelService channelService;
    private final UserService userService;

    public JCFMessageService(ChannelService channelService, UserService userService) {
        this.data = new HashMap<>();
        this.channelService = channelService;
        this.userService = userService;
    }

    // 등록
    @Override
    public Message createMessage(String content, UUID channelId, UUID userId) {
        try {
            channelService.getChannel(channelId);
            userService.getUser(userId);
        } catch (NoSuchElementException e) {
            throw e;
        }

        Message message = new Message(userId, channelId, content);
        data.put(message.getId(), message);

        return message;
    }

    // 단건 조회
    @Override
    public Message getMessage(UUID id) {
        Message messageNullable = this.data.get(id);

        return Optional.ofNullable(messageNullable)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + id + " not found"));
    }

    // 전체 조회
    @Override
    public List<Message> getAllMessages() {
        return new ArrayList<>(data.values());
    }

    // 이름 수정
    @Override
    public Message updateMessage(Message message, String newContent) {
        message.updateContent(newContent);
        return message;
    }

    // 삭제
    @Override
    public Message deleteMessage(Message message) {
        return data.remove(message.getId());
    }
}
