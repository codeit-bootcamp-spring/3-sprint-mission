package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> data = new HashMap<>();

    // 등록
    @Override
    public Message createMessage(Message message) {
        data.put(message.getId(), message);
        return message;
    }

    // 단건 조회
    @Override
    public Message getMessage(UUID id) {
        return data.get(id);
    }

    // 전체 조회
    @Override
    public List<Message> getAllMessages() {
        return new ArrayList<>(data.values());
    }

    // 채널로 조회
    @Override
    public List<Message> getMessagesByChannel(UUID channelId) {
        return data.values().stream()
                .filter(message -> message.getChannel().getId().equals(channelId))
                .collect(Collectors.toList());
    }

    // 이름 수정
    @Override
    public Message updateMessage(Message message, String newContent) {
        if (newContent != null && !newContent.isEmpty()) {
            message.updateContent(newContent);
        }
        data.put(message.getId(), message);
        return message;
    }

    // 삭제
    @Override
    public Message deleteMessage(Message message) {
        return data.remove(message.getId());
    }
}
