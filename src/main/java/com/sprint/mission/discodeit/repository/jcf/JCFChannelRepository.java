package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.dto.channel.*;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf")
public class JCFChannelRepository implements ChannelRepository {

    private final Map<UUID, Channel> channels;

    public JCFChannelRepository() {
        channels = new HashMap<UUID, Channel>();
    }

    @Override
    public Channel createChannel(Channel channel) {
        channels.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Optional<Channel> getChannel(GetPublicChannelRequestDto dto) {
        return Optional.ofNullable(channels.get(dto.getChannelId()));
    }

    @Override
    public Optional<Channel> getChannel(GetPrivateChannelRequestDto dto) {
        return Optional.ofNullable(channels.get(dto.getChannelId()));
    }

    @Override
    public List<Channel> findAllByUserId(UUID userId) {
        return channels.values().stream()
                .filter(channel -> isUserMemberOrAdmin(channel, userId))
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteChannel(UUID channelId, UUID userId) {
        return getChannel(new GetPublicChannelRequestDto(channelId))
                .filter(channel -> isAdmin(channel, userId))
                .map(channel -> {
                    cleanupChannelData(channel);
                    channels.remove(channelId);
                    return true;
                })
                .orElse(false);
    }

    @Override
    public boolean modifyChannel(Channel channel) {
        if (!channels.containsKey(channel.getId())) {
            return false;
        }
        channels.put(channel.getId(), channel);
        return true;
    }

    @Override
    public boolean kickOutChannel(UUID channelId, User kickUser, User admin) {
        return getChannel(new GetPublicChannelRequestDto(channelId))
                .filter(channel -> isAdmin(channel, admin.getId()))
                .map(channel -> {
                    channel.removeMember(kickUser);
                    return true;
                })
                .orElse(false);
    }

    @Override
    public boolean joinChannel(UUID channelId, User user) {
        return getChannel(new GetPublicChannelRequestDto(channelId))
                .filter(channel -> !channel.getMembers().contains(user))
                .map(channel -> {
                    channel.addMember(user);
                    return true;
                })
                .orElse(false);
    }

    private boolean isAdmin(Channel channel, UUID userId) {
        return channel.getChannelAdmin().getId().equals(userId);
    }

    private boolean isUserMemberOrAdmin(Channel channel, UUID userId) {
        return channel.getMembers().stream()
                .anyMatch(member -> member.getId().equals(userId)) ||
                isAdmin(channel, userId);
    }

    private void cleanupChannelData(Channel channel) {
        // 멤버들의 채널 참조 제거
        channel.getMembers().forEach(member ->
                member.getChannels().remove(channel)
        );
        // 채널의 멤버 및 메시지 초기화
        channel.getMembers().clear();
        channel.getMessages().clear();
    }
}
