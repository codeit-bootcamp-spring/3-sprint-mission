package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message createMessage(String content, UUID channelId, UUID authorId); // C
    Message readMessage(UUID id); // R
    List<Message> readAllMessages();
//    List<Message> readMessagesByChannelId(UUID channelId);
    Message updateMessage(UUID id, String newContent); // U
    void deleteMessage(UUID id); // D
}
