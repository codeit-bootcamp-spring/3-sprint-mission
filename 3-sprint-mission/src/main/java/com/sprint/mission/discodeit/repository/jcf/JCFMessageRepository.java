package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

//@Repository
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
    public List<Message> findAllByChannelId(UUID channelId) {

        return findAll().stream()
                .filter(m -> m.getChannelId().equals(channelId))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Message> findById(UUID id) {

        return findAll().stream()
                .filter(m -> m.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Message> findByContent(String content) {

        return findAll().stream()
                .filter(m -> m.getContent().contains(content))
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(UUID id) {
        return findById(id).isPresent();
    }

    @Override
    public void deleteById(UUID id) {
        this.data.removeIf(m -> m.getId().equals(id));

    }

    @Override
    public void deleteAllByChannelId(UUID channelId) {
        this.data.removeIf(m -> m.getChannelId().equals(channelId));
    }
}
