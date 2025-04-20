package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data; // Channel 데아터를 저장하는 필드

    public JCFChannelService() { this.data = new HashMap<>(); } // 기본 생성자

    @Override
    public Channel createChannel(String channelName, String description) {
        Channel channel = new Channel(channelName, description);
        data.put(channel.getId(), channel);

        return channel;
    }

    @Override
    public Channel readChannel(UUID channelId) {
        Channel channelNullable = this.data.get(channelId);
        return Optional.ofNullable(channelNullable)
                .orElseThrow(() -> new NoSuchElementException(channelId + "ID를 가진 채널이 존재하지 않습니다."));
    }

    @Override
    public List<Channel> readAllChannels() {
        return this.data.values().stream().toList();
    };

    @Override
    public Channel updateChannel(UUID id, String newName, String newDescription) { // U
        Channel channelNullable = this.data.get(id);
        Channel channel = Optional.ofNullable(channelNullable)
                .orElseThrow(() -> new NoSuchElementException(id + "ID를 가진 채널이 존재하지 않습니다."));
        channel.updateChannel(newName, newDescription);

        return channel;
    } // U

    @Override
    public void deleteChannel(UUID id) { // D
        if (!this.data.containsKey(id)) {
            throw new NoSuchElementException(id + "ID를 가진 채널을 찾을 수 없습니다.");
        }
        this.data.remove(id);
    };
}
