package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

public class JCFMessageService implements MessageService {
    // 저장 로직
    private final Map<UUID, Message> data = new HashMap<>();

    @Override
    public Message createMessage(Message message) {
        // 저장 로직
        data.put(message.getId(), message);
        return message;
    }

    @Override
    public Optional<Message> getMessage(UUID messageId) {
        // 저장 로직
        return Optional.ofNullable(data.get(messageId));
    }

    @Override
    public List<Message> getAllMessages() {
        // 저장 로직
        return new ArrayList<>(data.values());
    }

    // update 실패 시 피드백 출력
    @Override
    public void updateMessage(UUID messageId, String message) {
        // 비즈니스 로직 + 저장 로직
        getMessage(messageId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메시지"))
                .updateMsgContent(message);
    }

    @Override
    public void deleteMessage(UUID messageId) {
        // 저장 로직
        data.remove(messageId);
    }
}
