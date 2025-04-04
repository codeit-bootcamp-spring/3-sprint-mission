package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class JCFChannelService implements ChannelService {

    private final List<Channel> channelList;

    public JCFChannelService(List<Channel> channelList) {
        this.channelList = channelList;
    }

    public void create(Channel channel) {
        channelList.add(channel);
    }

    public List<Channel> readById(String channelName) {
        return channelList.stream()
                .filter(c -> c.getChannelName().equals(channelName))
                .collect(Collectors.toList());
    }

    public List<Channel> readAll() {
        return channelList;
    }

    public void update(String channelName, String ModifiedChannelName, String channelDescription,
                boolean isPrivate) {

        for (Channel c: channelList) {
            if (c.getChannelName().equals(channelName)) {
                c.update(ModifiedChannelName, channelDescription, isPrivate);
            }
        }
    }

    public void deleteById(String channelName) {
        channelList.removeIf(c -> c.getChannelName().equals(channelName));
    }

}
