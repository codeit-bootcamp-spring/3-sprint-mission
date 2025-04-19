package com.sprint.mission.discodeit.service.file;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

public class FileChannelService implements ChannelService {

    private final UserService userService;
    private final ChannelRepository channelRepository;

    public FileChannelService(UserService userService, ChannelRepository channelRepository) {
        this.userService = userService;
        this.channelRepository = channelRepository;
    }

    /**
     * 채널 생성
     *
     * @param channelName 채널 이름
     * @param isPrivate 채널 공개 여부
     * @param password 채널 비밀번호
     * @param channelOwnerId 채널 소유자 ID
     */
    @Override
    public Channel createChannel(String channelName, boolean isPrivate, String password, UUID channelOwnerId) {
        if (userService.getUserById(channelOwnerId) == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }
        Channel channel = new Channel(channelName, isPrivate, password, channelOwnerId);
        channel.addParticipant(channelOwnerId);
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
    public Set<UUID> getChannelParticipants(UUID channelId) {
        Channel channel = channelRepository.findById(channelId);
        if (channel == null) {
            throw new IllegalArgumentException("존재하지 않는 채널 ID입니다.");
        }
        return channel.getParticipants();
    }

    @Override
    public boolean joinChannel(UUID channelId, UUID userId, String password) {
        Channel channel = channelRepository.findById(channelId);
        if (channel == null) {
            throw new IllegalArgumentException("존재하지 않는 채널입니다.");
        }
        if (userService.getUserById(userId) == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
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

    /**
     * 채널 업데이트
     *
     * @param channelId 채널 ID
     * @param channelName 채널 이름
     * @param isPrivate 채널 공개 여부
     * @param password 채널 비밀번호
     */
    @Override
    public void updateChannel(UUID channelId, String channelName, boolean isPrivate, String password) {
        Channel channel = channelRepository.findById(channelId);
        if (channel == null) {
            throw new IllegalArgumentException("존재하지 않는 채널 ID입니다.");
        }
        if (channelName != null && !channelName.isEmpty()) {
            channel.updateChannelName(channelName);
        }
        channel.updatePrivate(isPrivate);
        if (password != null && !password.isEmpty()) {
            channel.updatePassword(password);
        }
        channelRepository.save(channel);

    }

    @Override
    public boolean leaveChannel(UUID channelId, UUID userId) {
        Channel channel = channelRepository.findById(channelId);
        if (channel == null) {
            throw new IllegalArgumentException("존재하지 않는 채널 ID입니다.");
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
    public void deleteChannel(UUID channelId) {
        channelRepository.deleteById(channelId);
    }
}
