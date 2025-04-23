package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;


public interface ChannelRepository {
    List<Channel> getChannelsList();
    void fileSaveChannels();
}
