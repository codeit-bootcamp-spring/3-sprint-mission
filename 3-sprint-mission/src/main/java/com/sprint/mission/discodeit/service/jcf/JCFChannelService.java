package com.sprint.mission.discodeit.service.jcf;

import java.util.*;
import java.util.stream.*;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

public class JCFChannelService implements ChannelService {
    public final List<Channel> data;
    public JCFChannelService(){
        this.data = new ArrayList<>();
    }

    @Override
    public void create(String name, User user) {
        this.data.add(new Channel(name, user));
    }

    @Override
    public List<Channel> read(String id) {
        return this.data.stream()
                .filter(c -> c.getId().contains(id))
                .collect(Collectors.toList());
    }

    @Override
    public List<Channel> readAll() {
        return this.data.stream()
                .collect(Collectors.toList());
    }

    public List<Channel> readByName(String name) {
        return this.data.stream()
                .filter(c -> c.getName().contains(name))
                .collect(Collectors.toList());
    }

    @Override
    public void update(String id, String name) {
        this.data.stream()
                .filter(c -> c.getId().contains(id))
                .forEach(c-> {
                    c.updateById(id,name);
                    c.updateDateTime();
                });
    }

    @Override
    public void delete(String id) {
        this.data.removeIf(u -> u.getId().contains(id));
    }
}
