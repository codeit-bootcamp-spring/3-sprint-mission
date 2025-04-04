package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data = new HashMap<>();

    //----------- 채널 생성 -----------
    @Override
    public Channel createChannel(String name) {
        Channel channel = new Channel(name);
        data.put(channel.getId(), channel);
        return channel;
    }

    //----------- 단일 채널 조회 -----------
    @Override
    public Channel getChannel(UUID id) {
        return data.get(id);
    }

    //----------- 모든 채널 조회 -----------
    @Override
    public List<Channel> getAllChannels() {
        return new ArrayList<>(data.values());
    }

    //----------- 채널 이름 수정 -----------
    @Override
    public void updateChannel(UUID id, String name) {
        Channel channel = data.get(id);
        if (channel != null) {
            channel.setChannelName(name);
        }
    }

    //----------- 채널 삭제 -----------
    @Override
    public void deleteChannel(UUID id) {
        data.remove(id);
    }

    //----------- 채널 검증 -----------
    @Override
    public boolean existsById(UUID id) {
        return data.containsKey(id);
    }
}
