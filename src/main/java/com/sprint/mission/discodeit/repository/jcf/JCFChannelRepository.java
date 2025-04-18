package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.*;

public class JCFChannelRepository implements ChannelRepository {
    private final Map<UUID, Channel> data;

    public JCFChannelRepository() {
        this.data = new HashMap<>();
    }

    public JCFChannelRepository(Map<UUID, Channel> data) {
        this.data = data;
    }

    /**
     * 채널 데이터를 저장하는 메서드
     * JCF*Repository의 경우 메모리에 저장되어 있으므로 해당 메서드 구현하지 않음
     */
    @Override
    public void saveAll() {
    }

    /**
     * 주어진 채널을 메모리에 저장하는 메서드
     *
     * @param channel 저장할 채널
     */
    @Override
    public void save(Channel channel) {
        data.put(channel.getId(), channel);
    }

    /**
     * 주어진 id에 해당하는 채널을 삭제하는 메서드
     *
     * @param id 삭제할 대상 채널 id
     */
    @Override
    public void delete(UUID id) {
        data.remove(id);
    }

    /**
     * 메모리에 저장되어있는 채널 데이터를 리턴하는 메서드
     *
     * @return 메모리에 저장된 채널데이터
     */
    public Map<UUID, Channel> readAll() {
        return data;
    }

    /**
     * 주어진 id에 해당하는 채널을 조회하는 메서드
     *
     * @param id 조회할 채널의 ID
     * @return 조회된 채널
     */
    @Override
    public Optional<Channel> readById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    /**
     * 주어진 채널명에 해당하는 채널을 조회하는 메서드
     *
     * @param channelName 조회할 채널명
     * @return 조회된 채널
     */
    @Override
    public Optional<Channel> readByChannelName(String channelName) {
        return data.values().stream()
                .filter(ch -> ch.getChannelName().equals(channelName))
                .findFirst();
    }
}
