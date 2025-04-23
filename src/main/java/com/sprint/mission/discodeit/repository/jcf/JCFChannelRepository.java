package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entitiy.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class JCFChannelRepository implements ChannelRepository {
    private final CopyOnWriteArrayList<Channel> data;

    public JCFChannelRepository(CopyOnWriteArrayList<Channel> channels) {
        this.data=channels;
    }

    @Override
    public void save(Channel channel) {
        data.add(channel);
    }

    @Override
    public void read() {
        data.stream()
                .forEach(System.out::println);
    }

    @Override
    public void readById(UUID id) {
        data.stream()
                .filter(n->n.getId().equals(id))
                .forEach(System.out::println);
    }

    @Override
    public void update(UUID id,Channel channel) {
        data.stream()
                .filter(chan -> chan.getId().equals(id))
                .forEach(chan->{
                    chan.updateUpdatedAt(Instant.now());
                    chan.updateChannelName(channel.getChannelName());
                    chan.updateMembers(channel.getMembers());
                });
    }

    @Override
    public void delete(Channel channel) {
        data.remove(channel);
    }
}
