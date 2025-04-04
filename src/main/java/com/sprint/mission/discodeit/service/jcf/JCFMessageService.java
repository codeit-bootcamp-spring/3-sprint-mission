package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;
import java.util.stream.Collectors;

public class JCFMessageService  implements MessageService {
    private final Map<UUID, Message> data = new HashMap<>();

    @Override
    public Message createMessage(UUID userId, UUID channelId, String content) {
        Message message = new Message(userId, channelId, content);
        data.put(message.getId(), message);
        return message;
    }

    @Override
    public Message getMessage(UUID id) {
        return data.get(id);
    }

//    @Override
//    public List<Message> getMessagesByChannel(UUID channelId) {
//        return messages.stream()
//                .filter(message -> message.getChannelId().equals(channelId))
//                .collect(Collectors.toList());
//    }

    @Override
    public List<Message> getAllMessages() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void updateMessage(UUID id, String content) {
        Message message = data.get(id);
        if (message != null) {
            message.update(content);
        }
    }

    @Override
    public void deleteMessage(UUID id) {
        data.remove(id);
    }
}
