package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.UUID;

public class FileMessageService implements MessageService {

    @Override
    public Message createMessage(UUID userId, UUID channelId, String content) {
        return null;
    }

    @Override
    public Message getMessage(UUID id) {
        return null;
    }

    @Override
    public List<Message> getMessagesByChannel(UUID channelId) {
        return List.of();
    }

    @Override
    public List<Message> getAllMessages() {
        return List.of();
    }

    @Override
    public void updateMessage(UUID id, String content) {

    }

    @Override
    public void deleteMessage(UUID id) {

    }
}
