package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;
import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message createMessage(String content, UUID channelId, UUID userId);
    Message getMessage(UUID id);
    List<Message> getAllMessages();
    Message updateMessage(Message message, String newContent);
    Message deleteMessage(Message message);
}
