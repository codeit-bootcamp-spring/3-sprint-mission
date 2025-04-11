package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JCFChannelService implements ChannelService {

    private final Map<Integer, Channel> channelMap;

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

    public void updateChannelName(String oldName, String newName) {
        for (Channel channel : channelMap.values()) {
            if (channel.getChannelName().equals(oldName)) {
                channel.updateChannel(newName);
                break;
            }
        }
    }

    public void deleteChannelName(String channelName) {
        Integer findChannelKey = null;
        for (Map.Entry<Integer, Channel> entry : channelMap.entrySet()) {
            if (entry.getValue().getChannelName().equals(channelName)) {
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
}
