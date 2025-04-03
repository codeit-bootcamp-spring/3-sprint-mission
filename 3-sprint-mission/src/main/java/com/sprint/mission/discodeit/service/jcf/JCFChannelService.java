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

    public List<Channel> readById(UUID id) {
        return channelList.stream()
                .filter(c -> c.getId().equals(id))
                .collect(Collectors.toList());
    }

    public List<Channel> readAll() {
        return channelList;
    }

    public void update(UUID id, String channelName, String channelDescription,
                boolean isPrivate) {

        for (Channel c: channelList) {
            if (c.getId().equals(id)) {
                c.update(channelName, channelDescription, isPrivate);
            }
        }
    }

    public void deleteById(UUID id) {
        channelList.removeIf(c -> c.getId().equals(id));
    }

}
