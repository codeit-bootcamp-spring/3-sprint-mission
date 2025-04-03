package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;

public class JCFChannelService {

    private final List<Channel> channelList;

    public JCFChannelService(List<Channel> channelList) {
        this.channelList = channelList;
    }
}
