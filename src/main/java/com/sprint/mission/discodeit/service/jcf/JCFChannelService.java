package com.sprint.mission.discodeit.service.jcf;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

public class JCFChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final UserService userService;

    public JCFChannelService(UserService userService, ChannelRepository channelRepository) {
        this.userService = userService;
        this.channelRepository = channelRepository;
    }

    @Override
    public Channel createChannel(String channelName, boolean isPrivate, String password, UUID ownerChannelId) {
        if (userService.getUserById(ownerChannelId) == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자 ID로는 채널을 생성할 수 없습니다.");
        }
        Channel channel = new Channel(channelName, isPrivate, password, ownerChannelId);
        channel.addParticipant(ownerChannelId);
        return channelRepository.save(channel);
    }

    @Override
    public Channel getChannelById(UUID channelId) {
        return channelRepository.findById(channelId);
    }

    @Override
    public List<Channel> getAllChannels() {
        return channelRepository.findAll();
    }

    @Override
    public void updateChannel(UUID channelId, String channelName, boolean isPrivate, String password) {
        Channel channel = channelRepository.findById(channelId);
        if (channel != null) {
            if (channelName != null && !channelName.isEmpty()) {
                channel.updateChannelName(channelName);
            }
            channel.updatePrivate(isPrivate);
            if (password != null && !password.isEmpty()) {
                channel.updatePassword(password);
            }
            channelRepository.save(channel);
        }
    }

    @Override
    public boolean joinChannel(UUID channelId, UUID userId, String password) {
        Channel channel = channelRepository.findById(channelId);
        if (channel == null) {
            throw new IllegalArgumentException("채널이 존재하지 않습니다.");
        }
        if (userService.getUserById(userId) == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자 ID로는 채널에 참가할 수 없습니다.");
        }
        if (channel.isParticipant(userId)) {
            return false;
        }
        if (channel.isPrivate() && !channel.getPassword().equals(password)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        channel.addParticipant(userId);
        channelRepository.save(channel);
        return true;
    }

    @Override
    public boolean leaveChannel(UUID channelId, UUID userId) {
        Channel channel = channelRepository.findById(channelId);
        if (channel == null) {
            throw new IllegalArgumentException("채널이 존재하지 않습니다.");
        }
        if (channel.getOwnerChannelId().equals(userId)) {
            throw new IllegalArgumentException("채널 소유자는 채널을 나갈 수 없습니다.");
        }
        boolean left = channel.removeParticipant(userId);
        if (left) {
            channelRepository.save(channel);
        }
        return left;
    }

    @Override
    public Set<UUID> getChannelParticipants(UUID channelId) {
        Channel channel = channelRepository.findById(channelId);
        if (channel == null) {
            throw new IllegalArgumentException("채널이 존재하지 않습니다.");
        }
        return channel.getParticipants();
    }

    @Override
    public void deleteChannel(UUID channelId) {
        channelRepository.deleteById(channelId);
    }
}
