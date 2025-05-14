package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageRepository {
    public Message createMessage(Message message, UUID channelId);

    public List<Message> getChannelMessages(UUID channelId);

    public boolean updateMessage(Message message, UUID channelId);

    public boolean deleteMessage(Message message, UUID channelId);
}
