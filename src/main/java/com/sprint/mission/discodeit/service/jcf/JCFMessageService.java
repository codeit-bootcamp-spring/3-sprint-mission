package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> data; //database

    public JCFMessageService() {
        this.data = new HashMap<>();
    }

    @Override
    public void create(Message msg) {
        this.data.put(msg.getId(), msg);
    }

    @Override
    public Message read(UUID id) {
        return this.data.get(id);
    }

    @Override
    public List<Message> readAll() {
        return new ArrayList<>(this.data.values());
    }

    @Override
    public Message update(UUID id, String text) {
        Message selected = this.data.get(id);
        selected.update(text);
        return selected;
    }

    @Override
    public boolean delete(UUID id) {
        this.data.remove(id);

        //TODO : update return value
        return true;
    }
}
