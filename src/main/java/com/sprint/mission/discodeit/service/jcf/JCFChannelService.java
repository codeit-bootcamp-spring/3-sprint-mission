package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.dto.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data = new HashMap<>();


    @Override
    public Channel createChannel(String name) {
        // 이름 중복 검사
        if (getChannelByName(name) != null) {
            throw new IllegalArgumentException("\n[Channel] 이미 존재하는 채널명입니다. (" + name + ")");
        }

        Channel channel = Channel.of(name);
        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Channel getChannel(UUID id) {
        return data.get(id);
    }

    @Override
    public List<Channel> getAllChannels() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Channel getChannelByName(String name) {
        return data.values().stream()
                .filter(channel -> channel.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void updateChannel(UUID id, String name) {
        Channel channel = data.get(id);
        if (channel != null) {
            channel.setName(name);
        }
    }

    @Override
    public void joinChannel(UUID userId, UUID channelId) {
        Channel channel = getChannel(channelId);
        if (channel != null) {
            channel.join(userId);
        }
    }

    @Override
    public void leaveChannel(UUID userId, UUID channelId) {
//        if (!existsById(channelId)) {
//            System.out.println("[Channel] 유효하지 않은 채널입니다.");
//        } else if (!userService.existsById(userId)) {
//            System.out.println("[Channel] 유효하지 않은 사용자입니다.");
//        } else {
//            getChannel(channelId).leave(userId);
//        }
    }

    @Override
    public void deleteChannel(UUID id) {
        data.remove(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return data.containsKey(id);
    }
}
