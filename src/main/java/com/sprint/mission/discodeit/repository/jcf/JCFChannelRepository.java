package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@Profile("jcf")
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
    public Channel update(UUID id, String name) {
        Channel channel = channels.get(id);
        if (channel == null) {
            throw new IllegalArgumentException("[Channel] 유효하지 않은 채널입니다. (channelId: " + id + ")");
        }

        channel.updateName(name);
        return channel;
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

    @Override
    public void delete(UUID id) {
        if (!channels.containsKey(id)) {
            throw new NoSuchElementException("[Channel] 유효하지 않은 사용자입니다. (userId: " + id + ")");
        }

        channels.remove(id);
    }
}