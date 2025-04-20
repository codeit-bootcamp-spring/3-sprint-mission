package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class BasicChannelService implements ChannelService {
    private final Map<UUID, Channel> channels = new HashMap<>();

    // 채널 생성
    @Override
    public Channel create(String name) {
        Channel channel = new Channel(name);
        channels.put(channel.getId(), channel);
        return channel;
    }

    // 채널 조회
    @Override
    public Channel findById(UUID id) {
        return channels.get(id);
    }

    // 전체 채널 조회
    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(channels.values());
    }

    // 채널 이름 수정
    @Override
    public Channel update(UUID id, String newName) {
        Channel channel = channels.get(id);
        if (channel != null) {
            channel.setName(newName);
            channel.updateUpdatedAt();
        }
        return channel;
    }

    // 채널 삭제
    @Override
    public void delete(UUID id) {
        channels.remove(id);
    }
}