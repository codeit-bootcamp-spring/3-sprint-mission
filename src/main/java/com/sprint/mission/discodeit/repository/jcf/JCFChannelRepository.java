package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import java.util.HashMap;
import java.util.Map;

public class JCFChannelRepository implements ChannelRepository {

    private final Map<Integer, Channel> channelMap;

    public JCFChannelRepository() {

        channelMap = new HashMap<>();
    }

    @Override
    public void saveChannel(Channel channel) {
        channelMap.put(channel.getChannelNumber(), channel);
    }

    @Override
    public Channel updateChannel(Channel channel) {
        if (channelMap.containsKey(channel.getChannelNumber())) {
            channelMap.put(channel.getChannelNumber(), channel);
            return channel;
        }
        return null;
    }

    @Override
    public void deleteChannel(int channelNumber) {
        channelMap.remove(channelNumber);
    }

    @Override
    public Channel findChannel(int channelNumber) {
        return channelMap.get(channelNumber);
    }

    @Override
    public Map<Integer, Channel> findAllChannel() {
        return channelMap;
    }
}