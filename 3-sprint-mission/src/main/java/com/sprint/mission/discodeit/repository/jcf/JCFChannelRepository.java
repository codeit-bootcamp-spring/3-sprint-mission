package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

public class JCFChannelRepository implements ChannelRepository {

    private final static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private final List<Channel> data;

    public JCFChannelRepository(){
        this.data = new ArrayList<>();
    }

    @Override
    public Channel save(Channel channel) {
        this.data.add(channel);
        return channel;
    }

    @Override
    public Channel find(UUID id) {

        return findAll().stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    @Override
    public List<Channel> findAll() {

        return this.data;
    }

    public List<Channel> findByName(String name) {

        return findAll().stream()
                .filter(c -> c.getName().contains(name))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID id) {
        this.data.removeIf(c -> c.getId().equals(id));
    }

}
