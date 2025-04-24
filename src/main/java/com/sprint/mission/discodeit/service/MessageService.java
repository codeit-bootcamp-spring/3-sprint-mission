package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageService {
    Message createMessage(Message message);
    Optional<Message> getMessage(UUID messageId);
    List<Message> getAllMessages();
    void updateMessage(UUID messageId, String msgContent);
    void deleteMessage(UUID messageId);
}
