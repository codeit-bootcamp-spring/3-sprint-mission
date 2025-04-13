package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

// Java Collections Framework 기반 채널 서비스 구현 클래스
public class JCFChannelService implements ChannelService {

    // 채널 데이터를 저장할 Map (key: UUID, value: Channel)
    private final Map<UUID, Channel> data = new HashMap<>();

    @Override
    public Channel create(String name) {
        // 새로운 채널 생성 후 저장소에 추가
        Channel channel = new Channel(name);
        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Channel findById(UUID id) {
        // 주어진 ID에 해당하는 채널 반환 (없으면 null)
        return data.get(id);
    }

    @Override
    public List<Channel> findAll() {
        // 저장된 모든 채널 리스트로 반환
        return new ArrayList<>(data.values());
    }

    @Override
    public Channel update(UUID id, String newName) {
        // ID로 채널을 찾아 이름을 변경
        Channel channel = data.get(id);
        if (channel != null) {
            channel.updateName(newName);
        }
        return channel; // null 또는 수정된 채널 반환
    }

    @Override
    public void delete(UUID id) {
        // ID에 해당하는 채널 삭제
        data.remove(id);
    }
}
