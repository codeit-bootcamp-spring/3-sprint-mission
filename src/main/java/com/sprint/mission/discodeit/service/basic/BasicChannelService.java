package com.sprint.mission.discodeit.service.basic;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final Map<UUID, Set<UUID>> channelParticipants = new HashMap<>();

    @Override
    public Channel createChannel(String channelName, boolean isPrivate, String password, UUID ownerUserId) {
        User owner = userRepository.findById(ownerUserId);
        if (owner == null) throw new IllegalArgumentException("존재하지 않는 소유자입니다.");
        Channel channel = new Channel(channelName, isPrivate, password, ownerUserId);
        Channel saved = channelRepository.save(channel);
        channelParticipants.put(saved.getChannelId(), new HashSet<>(Collections.singleton(ownerUserId)));
        return saved;
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
    public void updateChannel(UUID channelId, String newName, boolean isPrivate, String password) {
        Channel channel = channelRepository.findById(channelId);
        if (channel != null) {
            channel.updateChannelName(newName);
            channel.updatePrivate(isPrivate);
            channel.updatePassword(password);
            channelRepository.save(channel);
        }
    }

    @Override
    public void deleteChannel(UUID channelId) {
        channelRepository.deleteById(channelId);
        channelParticipants.remove(channelId);
    }

    @Override
    public boolean joinChannel(UUID channelId, UUID userId, String password) {
        Channel channel = channelRepository.findById(channelId);
        User user = userRepository.findById(userId);
        if (channel == null) throw new IllegalArgumentException("존재하지 않는 채널입니다.");
        if (user == null) throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        if (channel.isPrivate() && !Objects.equals(channel.getPassword(), password)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        Set<UUID> participants = channelParticipants.computeIfAbsent(channelId, k -> new HashSet<>());
        if (!participants.add(userId)) return false;
        return true;
    }

    @Override
    public boolean leaveChannel(UUID channelId, UUID userId) {
        Channel channel = channelRepository.findById(channelId);
        if (channel == null) throw new IllegalArgumentException("존재하지 않는 채널입니다.");
        if (channel.getOwnerChannelId().equals(userId)) {
            throw new IllegalArgumentException("채널 소유자는 나갈 수 없습니다.");
        }
        Set<UUID> participants = channelParticipants.get(channelId);
        if (participants == null || !participants.remove(userId)) return false;
        return true;
    }

    @Override
    public Set<UUID> getChannelParticipants(UUID channelId) {
        if (!channelParticipants.containsKey(channelId)) {
            throw new IllegalArgumentException("존재하지 않는 채널입니다.");
        }
        return Collections.unmodifiableSet(channelParticipants.get(channelId));
    }
}
