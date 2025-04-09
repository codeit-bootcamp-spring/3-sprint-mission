package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data = new HashMap<>();

    // 등록
    @Override
    public Channel createChannel(Channel channel) {
        data.put(channel.getId(), channel);
        return channel;
    }

    // 단건 조회
    @Override
    public Channel getChannel(UUID id) {
        return data.get(id);
    }

    // 전체 조회
    @Override
    public List<Channel> getAllChannels() {
        return new ArrayList<>(data.values());
    }

    // 이름 수정
    @Override
    public Channel updateChannel(Channel channel, String newName) {
        if (newName != null && !newName.isEmpty()) {
            channel.updateName(newName);
        }
        data.put(channel.getId(), channel);
        return channel;
    }

    // 삭제
    @Override
    public Channel deleteChannel(Channel channel) {
        return data.remove(channel.getId());
    }
}
