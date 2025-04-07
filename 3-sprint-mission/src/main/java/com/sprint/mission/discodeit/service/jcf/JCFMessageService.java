package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class JCFMessageService implements MessageService {

    private final List<Message> messageList;

    public JCFMessageService(List<Message> messageList) {
        this.messageList = messageList;
    }

    public void create(Message message) {
        messageList.add(message);
    }

    public List<Message> readById(UUID id) {
        return messageList.stream()
                .filter(m -> m.getId().equals(id))
                .collect(Collectors.toList());
    }

    public List<Message> readByChannelId(UUID id) {
        return messageList.stream()
                .filter(m -> m.getChannel().getId().equals(id))
                .collect(Collectors.toList());
    }

    public List<Message> readAll() {
        return messageList;
    }

    public void update(UUID id, String content) {

        for (Message m: messageList) {
            if (m.getId().equals(id)) {
                m.update(content);
            }
        }
    }

    public void deleteById(UUID id) {
        messageList.removeIf(m -> m.getId().equals(id));
    }

}
