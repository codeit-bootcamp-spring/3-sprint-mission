package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.dto.entity.Channel;
import com.sprint.mission.discodeit.dto.entity.Message;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;
import java.util.stream.Collectors;

public class JCFMessageService  implements MessageService {
    private final JCFChannelService channelService;
    private final Map<UUID, Message> data = new HashMap<>();

    public JCFMessageService(JCFChannelService channelService) {
        this.channelService = channelService;
    }

//    @Override
//    public Message createMessage(UUID userId, UUID channelId, String content) {
//        Channel channel = channelService.getChannel(channelId);
//
//        if (channel == null) {
//            System.out.println("[Message] 존재하지 않는 채널입니다. (channelId: " + channelId + ")");
//            return null;
//        }
//        if (!channel.isMember(userId)) {
//            System.out.println("[Message] 먼저 채널에 접속해주세요.");
//            return null;
//        }
//
//        Message message = Message.of(userId, channelId, content);
//        data.put(message.getId(), message);
//        return message;
//    }

    @Override
    public Message createMessage(MessageCreateRequest messageCreateRequest) {
        return null;
    }

    @Override
    public Message getMessage(UUID id) {
        return data.get(id);
    }

    @Override
    public List<Message> getMessagesByChannel(UUID channelId) {
        return data.values().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .sorted(Comparator.comparing(Message::getCreatedAt))
                .collect(Collectors.toList());
    }

    @Override
    public List<Message> getAllMessages() {
        return data.values().stream()
                .sorted(Comparator.comparing(Message::getCreatedAt))
                .collect(Collectors.toList());
    }

    @Override
    public void updateMessage(UUID id, String content) {
        Message message = data.get(id);
        if (message != null) {
            message.update(content);
        }
    }

    @Override
    public void deleteMessage(UUID id) {
        data.remove(id);
    }
}
