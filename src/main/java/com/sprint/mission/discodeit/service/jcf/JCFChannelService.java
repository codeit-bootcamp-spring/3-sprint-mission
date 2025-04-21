package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel; // Channel 클래스 가져오기
import com.sprint.mission.discodeit.service.ChannelService; // ChannelService 인터페이스 가져오기

import java.util.*;

// JCFChannelService는 ChannelService를 실제로 구현한 클래스
public class JCFChannelService implements ChannelService {

    // 채널 데이터를 저장하는 공간
    private final Map<UUID, Channel> data = new HashMap<>();

    @Override // 채널을 새로 등록하는 기능
    public Channel create(Channel channel) {
        data.put(channel.getId(), channel);
        return channel;
    }

    @Override // ID로 특정 채널 하나만 조회하는 기능
    public Channel getById(UUID id) {
        return data.get(id);
    }

    @Override // 모든 채널 목록을 리스트 형태로 가져오는 기능
    public List<Channel> getAll() {
        return new ArrayList<>(data.values());
    }

    @Override // 채널 정보를 수정하는 기능
    public Channel update(Channel channel) {
        data.put(channel.getId(), channel);
        return channel;
    }

    @Override // ID로 특정 채널을 삭제하는 기능
    public void delete(UUID id) {
        data.remove(id);
    }
}
