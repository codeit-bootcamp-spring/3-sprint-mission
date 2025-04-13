package com.sprint.mission.discodeit.service.jcf;

import java.util.*;
import java.util.stream.Collectors;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.MessageService;

public class JCFMessageService implements MessageService {
    public final List<Message> data;
    public JCFMessageService(){
        data = new ArrayList<>();
    }

    @Override
    public void create(String text, User user, Channel channel) {
        this.data.add(new Message(text, user, channel));
    }

    @Override
    public Message read(UUID id) {
        return this.data.stream()
                .filter(m -> m.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 메시지는 존재하지 않습니다."));
    }

    @Override
    public List<Message> readAll() {
        return this.data.stream()
                .collect(Collectors.toList());
    }

    public List<Message> readByText(String text) {
        return this.data.stream()
                .filter(m -> m.getText().contains(text))
                .collect(Collectors.toList());
    }

    public List<Message> readBySender(String sendername) {
        return this.data.stream()
                .filter(m -> m.getSender().getName().equals(sendername))
                .collect(Collectors.toList());
    }

    @Override
    public void update(UUID id, String text) {
        this.data.stream()
                .filter(m -> m.getId().equals(id))
                .forEach(m -> {
                    m.updateById(id, text);
                    m.updateDateTime();
                });
    }

    @Override
    public void delete(UUID id) {
        this.data.removeIf(m -> m.getId().equals(id));
    }
}
