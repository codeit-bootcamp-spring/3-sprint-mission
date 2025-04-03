package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class JCFMessageService {

    private final List<Message> messageList;

    public JCFMessageService(List<Message> messageList) {
        this.messageList = messageList;
    }

    public void create(Message message) {
        messageList.add(message);
    }

    List<Message> readById(UUID id) {
        return messageList.stream()
                .filter(m -> m.getId().equals(id))
                .collect(Collectors.toList());
    }

    List<Message> readAll() {
        return messageList;
    }

    void update(UUID id, String content) {

        for (Message m: messageList) {
            if (m.getId().equals(id)) {
                m.update(content);
            }
        }
    }

    void deleteById(UUID id) {
        messageList.removeIf(m -> m.getId().equals(id));
    }

}
