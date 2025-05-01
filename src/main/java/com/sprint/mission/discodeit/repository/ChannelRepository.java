package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import java.util.Map;

public interface ChannelRepository {

    void saveChannel(Channel channel);

    Channel updateChannel(Channel channel);

    void deleteChannel(int channelNumber);

    Channel findChannel(int channelNumber);

    Map<Integer, Channel> findAllChannel();
}