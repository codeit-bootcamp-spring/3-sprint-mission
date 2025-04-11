package com.sprint.mission.discodeit.service.jcf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

public class JCFMessageService implements MessageService {

    // 데이터를 저장하는 필드 (ID를 키로 사용)
    private final Map<UUID, Message> data;
    private final UserService userService;       // UserService 의존성 추가
    private final ChannelService channelService; // ChannelService 의존성 추가

    // 생성자: HashMap으로 초기화 및 UserService, ChannelService 주입
    public JCFMessageService(UserService userService, ChannelService channelService) {
        this.data = new HashMap<>();
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message createMessage(String content, UUID authorId, UUID channelId) {
        // 작성자 존재 여부 확인
        if (userService.getUserById(authorId) == null) {
            throw new IllegalArgumentException("존재하지 않는 작성자 ID입니다.");
        }
        // 채널 존재 여부 확인
        Channel channel = channelService.getChannelById(channelId);
        if (channel == null) {
            throw new IllegalArgumentException("존재하지 않는 채널 ID입니다.");
        }
        // 작성자가 채널 참가자인지 확인 (채널 서비스에 주입된 user service 사용)
        if (!channel.isParticipant(authorId)) {
            throw new IllegalStateException("채널 참가자만 메시지를 작성할 수 있습니다.");
        }

        Message message = new Message(content, authorId, channelId);
        data.put(message.getMessageId(), message);
        return message;
    }

    @Override
    public Message getMessageById(UUID messageId) {
        return data.get(messageId); // ID로 메시지 조회
    }

    @Override
    public List<Message> getMessagesByChannel(UUID channelId) {
        // 특정 채널의 메시지 필터링
        return data.values().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Message> getMessagesByAuthor(UUID authorId) {
        // 특정 작성자의 메시지 필터링
        return data.values().stream()
                .filter(message -> message.getAuthorId().equals(authorId))
                .collect(Collectors.toList());
    }

    @Override
    public void updateMessage(UUID messageId, String updatedContent) {
        Message message = data.get(messageId);
        if (message != null) {
            message.updateContent(updatedContent);
        } else {
            throw new IllegalArgumentException("존재하지 않는 메시지 ID입니다.");
        }
    }

    @Override
    public void deleteMessage(UUID messageId) {
        Message message = data.get(messageId);
        if (message == null) {
            throw new IllegalArgumentException("존재하지 않는 메시지 ID입니다.");
        }
        data.remove(messageId); // 메시지 데이터 삭제
    }
}
