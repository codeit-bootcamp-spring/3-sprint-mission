package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class FileChannelService implements ChannelService {
    private final FileDataStore<Channel> store;
    private final Map<UUID, Channel> data;
    private final UserService userService;

    public FileChannelService(UserService userService) {
        this.store = new FileDataStore<>("data/channels.ser");
        this.data = store.load();
        this.userService = userService;
    }

    @Override
    public Channel createChannel(String name, UUID userId) {
        userService.getUser(userId); // 유효성 검사
        Channel channel = new Channel(name, userId);
        data.put(channel.getId(), channel);
        store.save(data);
        return channel;
    }

    @Override
    public Channel getChannel(UUID id) {
        Channel channel = data.get(id);
        if (channel == null) throw new NoSuchElementException("Channel not found: " + id);
        return channel;
    }

    @Override
    public List<Channel> getAllChannels() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Channel updateChannel(Channel channel, String newName) {
        channel.updateName(newName);
        store.save(data);
        return channel;
    }

    @Override
    public Channel deleteChannel(Channel channel) {
        Channel removed = data.remove(channel.getId());
        store.save(data);
        return removed;
    }
}
