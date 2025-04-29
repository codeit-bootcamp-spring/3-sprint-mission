package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.*;

// TODO 나중에 Repository객체를 연결해야함
public class JCFChannelRepository implements ChannelRepository {
    Map<UUID, Channel> channelMap;

    public JCFChannelRepository() {
        this.channelMap = new HashMap<UUID, Channel>();
    }

    @Override
    public void create(Channel channel) { // Channel channel1 = new Channel(id, name, description)을 받아서 파라미터로 쓰면 됨
        this.channelMap.put(channel.getId(), channel);
    }

    @Override
    public Channel findById(UUID id) {

        return this.channelMap.get(id);
    }

    @Override
    public List<Channel> findAll() {
        List<Channel> channels = new ArrayList<>(channelMap.values());

        return channels;
    }

    @Override
    public void update(UUID id, String newName, String newDescription) {
        Channel channel = this.channelMap.get(id);
        if (channel != null) {
            channel.updateChannel(newName, newDescription);
        }
    }

    public void delete(UUID id) {
//        if (!this.channelMap.containsKey(id)) {
//            throw new NoSuchElementException(id + "ID를 가진 채널을 찾을 수 없습니다."); // 비즈니스 로직
//        }
        this.channelMap.remove(id);
    }

}
