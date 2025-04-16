package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> data = new HashMap<>();

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

    // update 실패 시 피드백 출력
    public void updateMessage(UUID messageId, String message) {
        getMessage(messageId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메시지"))
                .updateMsgContent(message);
    }

    public void deleteMessage(UUID messageId) {
        data.remove(messageId);
    }

}
