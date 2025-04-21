package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.*;

public class JCFChannelRepository implements ChannelRepository {
    private final Map<UUID, Channel> storage = new HashMap<>();

    // 채널 저장 (신규 생성 또는 수정 시 호출)
    @Override
    public void save(Channel channel) {
        storage.put(channel.getId(), channel); // ID를 기준으로 저장 (기존 ID면 덮어씀)
    }

    // 채널 조회
    @Override
    public Channel findById(UUID id) {
        return storage.get(id); // 해당 ID의 채널 반환 (없으면 null)
    }

    // 모든 채널 조회
    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(storage.values()); // Map의 value들을 리스트로 반환
    }

    // 채널 정보 갱신
    @Override
    public void update(Channel channel) {
        storage.put(channel.getId(), channel); // ID 기준으로 덮어쓰기
    }

    // 채널 삭제
    @Override
    public void delete(UUID id) {
        storage.remove(id); // 해당 ID의 채널 삭제
    }
}