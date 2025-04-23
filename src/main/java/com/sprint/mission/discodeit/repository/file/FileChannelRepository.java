package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.file.FileDataStore;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public class FileChannelRepository implements ChannelRepository {
    private final FileDataStore<Channel> store;
    private final Map<UUID, Channel> data;

    public FileChannelRepository() {
        File dir = new File("data");
        if (!dir.exists()) dir.mkdirs();

        this.store = new FileDataStore<>("data/channels.ser");
        this.data = store.load();
    }

    @Override
    public Channel save(Channel channel) {
        data.put(channel.getId(), channel);
        store.save(data);
        return channel;
    }

    @Override
    public Channel findById(UUID id) {
        return Optional.ofNullable(data.get(id))
                .orElseThrow(() -> new NoSuchElementException("Channel not found: " + id));
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Channel delete(Channel channel) {
        Channel removed = data.remove(channel.getId());
        store.save(data);
        return removed;
    }
}