package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    // 채널 데이터를 저장하는 Map (key: 채널 ID, value: Channel 객체)
    private final Map<UUID, Channel> data;

    // 생성자: 데이터 저장용 Map 초기화
    public JCFChannelService() {
        this.data = new HashMap<>();
    }

    // 채널 생성 및 저장
    @Override
    public Channel create(String name) {
        Channel channel = new Channel(name);
        data.put(channel.getId(), channel);
        return channel;
    }

    // ID로 채널 조회
    @Override
    public Channel findById(UUID id) {
        return data.get(id);
    }

    // 모든 채널 목록 조회
    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    // 채널 이름 수정
    @Override
    public void update(UUID id, String newName) {
        Channel channel = data.get(id);
        if (channel != null) {
            channel.updateName(newName);
        }
    }

    // 채널 삭제
    @Override
    public void delete(UUID id) {
        data.remove(id);
    }
}
