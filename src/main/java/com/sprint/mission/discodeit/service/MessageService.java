package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.entity.Message;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message createMessage(MessageCreateRequest messageCreateRequest);
    Message getMessage(UUID id);
    List<Message> getMessagesByChannel(UUID channelId);
    List<Message> getAllMessages();
    void updateMessage(UUID id, String content);
    void deleteMessage(UUID id);
}