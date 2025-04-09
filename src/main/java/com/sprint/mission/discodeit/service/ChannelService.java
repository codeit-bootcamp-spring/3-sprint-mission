package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

public interface ChannelService {

    public void outputAllChannelInfo();

    public void outputOneChannelInfo(Channel channel);

    public void updateChannelName(String oldName, String newName);

    public void deleteChannelName(String channelName);

    public void createNewChannel(String channelName);

    public Channel changeChannel(int channelNumber);
}
