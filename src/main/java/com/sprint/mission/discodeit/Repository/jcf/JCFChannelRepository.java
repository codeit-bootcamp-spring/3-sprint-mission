package com.sprint.mission.discodeit.Repository.jcf;

import com.sprint.mission.discodeit.Repository.ChannelRepository;
import com.sprint.mission.discodeit.entity.Channel;

import java.util.*;


public class JCFChannelRepository implements ChannelRepository {
    private final Map<UUID, Channel> channels = new HashMap<>();

    @Override
    public void save(Channel ch) {
        channels.put(ch.getId(), ch);
    }

    @Override
    public Channel loadByName(String name) {
        return channels.values().stream()
                .filter(ch -> ch.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Channel loadById(UUID id) {
        return channels.get(id);
    }

    @Override
    public List<Channel> loadAll() {
        return channels.values().stream().toList();
    }

    @Override
    public void update(UUID id, String name) {
        Channel ch = channels.get(id);
        if (ch == null) {
            throw new IllegalArgumentException("[Channel] 유효하지 않은 채널입니다. (channelId: " + id + ")");
        }

        ch.setName(name);
    }

    @Override
    public void delete(UUID id) {
        if (!channels.containsKey(id)) {
            throw new NoSuchElementException("[Channel] 유효하지 않은 사용자입니다. (userId: " + id + ")");
        }

        channels.remove(id);
    }

    @Override
    public void join(UUID userId, UUID channelId) {
        Channel ch = loadById(channelId);

        if (ch == null) {
            throw new IllegalArgumentException("[Channel] 유효하지 않은 채널입니다. (channelId: " + channelId + ")");
        }

        ch.join(userId);
        save(ch);
        System.out.println("[Channel] 채널에 접속했습니다. (userId: " + userId + ", channelId: " + channelId + ")");
    }

    @Override
    public void leave(UUID userId, UUID channelId) {
        Channel ch = loadById(channelId);

        if (ch == null) {
            throw new IllegalArgumentException("[Channel] 유효하지 않은 채널입니다. (channelId: " + channelId + ")");
        }

        ch.leave(userId);
        save(ch);
        System.out.println("[Channel] 채널에서 탈퇴했습니다. (userId: " + userId + ", channelId: " + channelId + ")");
    }
}