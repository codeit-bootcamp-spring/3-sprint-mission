package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.Map;
import java.util.UUID;

public interface MessageService {
    Message createMessage(String text,UUID channelID, UUID userID);

    Map<UUID, Message> readMessages();

    Message readMessage(UUID id);

    Message updateMessage(UUID id, String text);
    Message deleteMessage(UUID id);
}
