package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;


@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
@Repository
public class JCFChannelRepository implements ChannelRepository {

    private final Map<UUID, Channel> channels = new LinkedHashMap<>();

    // 채널 저장
    @Override
    public Channel saveChannel(Channel channel) {
        channels.put(channel.getId(), channel);

        return channel;
    }

    // 전체 채널 조회
    @Override
    public List<Channel> findAllChannels() {
        return new ArrayList<>(channels.values());
    }

    // 이름으로 채널 조회
    @Override
    public Optional<Channel> findChannelUsingName(String channelName) {
        return channels.values().stream()
                .filter(channel -> channel.getChannelName().equalsIgnoreCase(channelName))
                .findFirst();
    }

    // 아이디로 채널 조회
    @Override
    public Optional<Channel> findChannelUsingId(UUID channelId) {
        return Optional.of(channels.get(channelId));
    }

    // 채널 삭제
    @Override
    public void deleteChannel(UUID channelId) {
        channels.remove(channelId);
    }
}
