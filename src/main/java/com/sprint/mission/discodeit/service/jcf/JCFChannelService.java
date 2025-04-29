package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFChannelService implements ChannelService {

    private final Map<UUID, Channel> data;
    private final UserService userService;

    public JCFChannelService(UserService userService) {
        this.data = new HashMap<>();
        this.userService = userService;
    }

    @Override
    public void save(Channel channel) {
        if (userService.findById(channel.getChannelMaster()).isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }

        data.put(channel.getId(), channel);

        userService.findById(channel.getChannelMaster()).ifPresent(user -> {
            // 채널 주인은 채널 생성 시 채널에 입장
            user.getChannels().add(channel.getId());
            channel.getUsers().add(user.getId());
            userService.update(user);
        });
    }

    @Override
    public Optional<Channel> findById(UUID channelId) {
        Optional<Channel> foundChannel = data.entrySet().stream()
                .filter(entry -> entry.getKey().equals(channelId))
                .map(Map.Entry::getValue)
                .findFirst();

        return foundChannel;
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Channel update(Channel channel) {
        data.put(channel.getId(), channel);

        return channel;
    }

    @Override
    public void deleteById(UUID channelId) {
        Channel channel = data.get(channelId);

        if (channel != null) {
            // 채널에 속한 유저의 채널 리스트에서 채널 삭제
            for (UUID id : channel.getUsers()) {
                userService.findById(id).ifPresent(user -> {
                    user.getChannels().removeIf(ch -> ch.equals(channelId));
                    userService.update(user);
                });

            }
        }

        data.remove(channelId);
    }
}
