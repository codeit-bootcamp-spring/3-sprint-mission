package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;

public class JCFChannelService implements ChannelService {

    private final List<Channel> channels;

    public JCFChannelService(List<Channel> channels) {
        this.channels = channels;
    }

    @Override
    public void outputAllChannelInfo() {
        for (Channel channel : channels) {
            System.out.println(channel);
        }
    }

    public void outputOneChannelInfo(Channel channel) {
        if (channel == null) {
            System.out.println("해당 채널은 존재하지 않습니다.");
        } else {
            System.out.println(channel);
        }
    }

    public void updateChannelName(String oldName, String newName) {
        channels.stream()
                .filter(channel -> channel.getChannelName().equals(oldName))
                .findFirst()
                .ifPresent(channel1 -> channel1.updateChannel(newName));
    }

    public void deleteChannelName(String channelName) {
        channels.stream()
                .filter(channel1 -> channel1.getChannelName().equals(channelName))
                .findFirst()
                .ifPresent(channel1 -> channels.remove(channel1));
    }

    public void createNewChannel(String channelName) {
        Channel channel = new Channel(channelName);
        channels.add(channel);
    }

    public Channel changeChannel(int channelNumber) {
        return channels.stream().filter(channel1 -> channel1.getChannelNumber() == channelNumber).findFirst()
                .orElse(null);
    }
}
