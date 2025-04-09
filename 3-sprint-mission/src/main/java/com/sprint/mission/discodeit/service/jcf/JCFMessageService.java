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
    public List<Message> read(String id) {
        return this.data.stream()
                .filter(m -> m.getId().contains(id))
                .collect(Collectors.toList());
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

    public List<Message> readBySender(String sender) {
        return this.data.stream()
                .filter(m -> m.getSender().contains(sender))
                .collect(Collectors.toList());
    }

    @Override
    public void update(String id, String text) {
        this.data.stream()
                .filter(m -> m.getId().contains(id))
                .forEach(m -> {
                    m.updateById(id, text);
                    m.updateDateTime();
                });
    }

    @Override
    public void delete(String id) {
        this.data.removeIf(m -> m.getId().contains(id));
    }
}
