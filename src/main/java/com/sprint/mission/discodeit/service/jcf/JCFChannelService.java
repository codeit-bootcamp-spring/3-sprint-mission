package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entitiy.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.UUID;

public class JCFChannelService implements ChannelService {

    private final List<Channel> data;

    public JCFChannelService(List<Channel> channels) {
        this.data=channels;
    }

    @Override
    public void create(Channel channel) {
        data.add(channel);
    }

    @Override
    public void readAll() {
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
    public void updateById(UUID id,Channel channel) {
        data.stream()
                .filter(chan -> chan.getId().equals(id))
                .forEach(chan->{
                    chan.updateUpdatedAt(System.currentTimeMillis());
                    chan.updateChannelName(channel.getChannelName());
                    chan.updateMembers(channel.getMembers());
                });
    }

    @Override
    public void delete(Channel channel) {
        data.remove(channel);
    }
}
