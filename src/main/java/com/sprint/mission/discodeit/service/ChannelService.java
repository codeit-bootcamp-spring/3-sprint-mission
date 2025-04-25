package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

public interface ChannelService {

    public void outputAllChannelInfo();

    public void outputOneChannelInfo(Channel channel);

    public void updateChannelName(Channel currentChannel, String newName);

    public void deleteChannelName(Channel currentChannel);

    public void createNewChannel(String channelName);

    public Channel getChannelByNumber(int channelNumber);

    public void selectChannel(int channelNumber);

    public Channel getCurrentChannel();

}
