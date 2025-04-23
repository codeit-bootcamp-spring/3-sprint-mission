package com.sprint.mission.discodeit.repository.jcf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

public class JCFChannelRepository implements ChannelRepository {
    private static JCFChannelRepository instance;
    private final Map<UUID, Channel> data = new HashMap<>();

    private JCFChannelRepository() {}

    public static JCFChannelRepository getInstance() {
        if (instance == null) {
            instance = new JCFChannelRepository();
        }
        return instance;
    }

    // 테스트용 메서드
    public static void clearInstance() {
        if (instance != null) {
            instance.clearData();
            instance = null;
        }
    }

    public void clearData() {
        data.clear();
    }

    @Override
    public Channel save(Channel channel) {
        data.put(channel.getChannelId(), channel);
        return channel;
    }

    @Override
    public Channel findById(UUID channelId) {
        return data.get(channelId);
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void deleteById(UUID channelId) {
        data.remove(channelId);
    }
}
