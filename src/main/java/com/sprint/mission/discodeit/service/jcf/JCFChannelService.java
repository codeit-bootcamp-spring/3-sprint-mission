package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data; // Channel 데아터를 저장하는 필드

    public JCFChannelService() { this.data = new HashMap<>(); } // 기본 생성자

    @Override
    public Channel createChannel(Channel channel) {
        UUID channelId = channel.getId();
        data.put(channelId, channel);
        return channel; //
    }

    @Override
    public Channel readChannel(UUID id) {
        return data.get(id);
    }

    @Override
    public List<Channel> readAllChannels() {
        return new ArrayList<>(data.values());
    };

    @Override
    public void updateChannel(UUID id, String newName) { // U
        Channel channel = data.get(id);
        if (channel != null) {
            channel.updateChannelName(newName);
            System.out.println("이름을 " + newName + "으로 수정했습니다.");
        } else {
            System.out.println("해당 사용자가 존재하지 않습니다.");
        }
    } // U

    @Override
    public void deleteChannel(UUID id) { // D
        Channel channel = data.get(id);
        data.remove(channel.getId()); // ID를 불러와서 없앰
    };
}
