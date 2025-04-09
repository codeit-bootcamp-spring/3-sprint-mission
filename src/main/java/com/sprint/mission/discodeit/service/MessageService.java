package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;
import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message createMessage(Message message);
    Message getMessage(UUID id);
    List<Message> getAllMessages();
    List<Message> getMessagesByChannel(UUID channelId);
    Message updateMessage(Message message, String newContent);
    Message deleteMessage(Message message);
}
