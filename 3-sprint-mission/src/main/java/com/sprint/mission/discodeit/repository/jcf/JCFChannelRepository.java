package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

//@Repository
public class JCFChannelRepository implements ChannelRepository {
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
    public Optional<Channel> findById(UUID id) {

        return findAll().stream()
                .filter(c -> c.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Channel> findAll() {

        return this.data;
    }

    public List<Channel> findByName(String name) {

        return findAll().stream()
                .filter(c -> c.getName().contains(name))
                .toList();
    }

    public boolean existsById(UUID id) {
        return findAll().stream().anyMatch(c -> c.getId().equals(id));
    }

    public boolean existsByName(String name) {
        return findAll().stream().anyMatch(c -> c.getName().contains(name));
    }

    @Override
    public void deleteById(UUID id) {
        this.data.removeIf(c -> c.getId().equals(id));
    }

}
