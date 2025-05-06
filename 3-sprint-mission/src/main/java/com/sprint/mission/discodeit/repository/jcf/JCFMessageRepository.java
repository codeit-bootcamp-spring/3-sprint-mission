package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

public class JCFMessageRepository implements MessageRepository {
    private final static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private final List<Message> data;

    public JCFMessageRepository(){
        data = new ArrayList<>();
    }

    @Override
    public Message save(Message message) {
        this.data.add(message);
        return message;
    }

    @Override
    public List<Message> findAll() {
        return this.data;
    }

    @Override
    public List<Message> findAllFromChannel(UUID currentChannel) {

        return findAll().stream()
                .filter(m -> m.getChannel().equals(currentChannel))
                .collect(Collectors.toList());
    }

    @Override
    public Message find(UUID id) {

        return findAll().stream()
                .filter(m -> m.getId().equals(id))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    @Override
    public List<Message> findByText(String text) {

        return findAll().stream()
                .filter(m -> m.getText().contains(text))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID id) {
        this.data.removeIf(m -> m.getId().equals(id));

    }
}
