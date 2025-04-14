package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;
import java.util.stream.Collectors;

public class JCFMessageService  implements MessageService {
    JCFUserService userService;
    JCFChannelService channelService;
    private final Map<UUID, Message> data = new HashMap<>();

    public JCFMessageService(JCFUserService userService, JCFChannelService channelService) {
        this.userService = userService;
        this.channelService = channelService;
    }

    //----------- 메시지 생성 -----------
    @Override
    public Message createMessage(UUID userId, UUID channelId, String content) {
        //검증 로직
        if (!userService.existsById(userId)) {
            System.out.println("[Message] 존재하지 않는 사용자입니다.");
            return null;
        }

        if (!channelService.existsById(channelId)) {
            System.out.println("[Message] 존재하지 않는 채널입니다.");
            return null;
        }

        Channel channel = channelService.getChannel(channelId);
        if (!channel.isMember(userId)) {
            System.out.println("[Message] 먼저 채널에 접속해주세요.");
            return null;
        }

        Message message = new Message(userId, channelId, content);
        data.put(message.getId(), message);
        return message;
    }

    //----------- 단일 메시지 조회 -----------
    @Override
    public Message getMessage(UUID id) {
        return data.get(id);
    }


    //----------- 채널 별 메시지 조회 -----------
    @Override
    public List<Message> getMessagesByChannel(UUID channelId) {
        return data.values().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .sorted(Comparator.comparing(Message::getCreatedAt))
                .collect(Collectors.toList());
    }

    //----------- 모든 메시지 조회 -----------
    @Override
    public List<Message> getAllMessages() {
        return data.values().stream()
                .sorted(Comparator.comparing(Message::getCreatedAt))
                .collect(Collectors.toList());
    }

    //----------- 메시지 수정 -----------
    @Override
    public void updateMessage(UUID id, String content) {
        Message message = data.get(id);
        if (message != null) {
            message.update(content);
        }
    }

    //----------- 메시지 삭제 -----------
    @Override
    public void deleteMessage(UUID id) {
        data.remove(id);
    }
}
