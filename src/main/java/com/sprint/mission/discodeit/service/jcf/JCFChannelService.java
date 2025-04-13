package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JCFChannelService implements ChannelService {

    private final Map<Integer, Channel> channelMap;
    private Channel currentChannel;

    public JCFChannelService(List<Channel> channels) {
        this.channelMap = new HashMap<>();
        for (Channel channel : channels) {
            channelMap.put(channel.getChannelNumber(), channel);
        }
    }

    @Override
    public void outputAllChannelInfo() {
        for (Channel channel : channelMap.values()) {
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

    public void updateChannelName(Channel currentChannel, String newName) {
        Channel channel = channelMap.get(currentChannel.getChannelNumber());
        if (channel != null) {
            channel.updateChannel(newName);
        } else {
            System.out.println("해당 채널을 찾을 수 없습니다.");
        }
    }

    public void deleteChannelName(Channel currentChannel) {
        Integer findChannelKey = null;
        for (Map.Entry<Integer, Channel> entry : channelMap.entrySet()) {
            if (entry.getValue().getChannelNumber() == (currentChannel.getChannelNumber())) {
                findChannelKey = entry.getKey();
                break;
            }
        }
        if (findChannelKey != null) {
            channelMap.remove(findChannelKey);
        }
    }

    public void createNewChannel(String channelName) {
        Channel channel = new Channel(channelName);
        channelMap.put(channel.getChannelNumber(), channel);
    }

    public Channel changeChannel(int channelNumber) {
        return channelMap.getOrDefault(channelNumber, null);
    }

    public void selectChannel(int channelNumber) {
        Channel channel = channelMap.get(channelNumber);
        if (channel != null) {
            currentChannel = channel;
        } else {
            System.out.println("유효하지 않은 채널 번호입니다.");
        }
    }

    public Channel getCurrentChannel() {
        return currentChannel;
    }
}