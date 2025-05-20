package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
@Repository
public class JCFChannelRepository implements ChannelRepository {
    private final Map<UUID,Channel> channels;
    public JCFChannelRepository() {
        this.channels = new HashMap<>();
    }
    //

    @Override
    public Channel save(Channel channel) {
        this.channels.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Channel findById(UUID channelId) {
        if(channels.containsKey(channelId)){
            return channels.get(channelId);
        }
        return null;
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(this.channels.values());
    }

    @Override
    public boolean isExistName(String name) {
        return channels.values().stream().anyMatch(c -> c.getName().equalsIgnoreCase(name));
    }

    @Override
    public void delete(UUID channelId) {
        this.channels.remove(channelId);
    }
}
