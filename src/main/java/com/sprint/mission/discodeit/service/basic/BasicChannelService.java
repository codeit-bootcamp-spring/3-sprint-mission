package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    private final ChannelMapper channelMapper;

    @Transactional
    @Override
    public ChannelResponse createPublicChannel(PublicChannelCreateRequest request) {
        Channel channel = new Channel(ChannelType.PUBLIC, request.name(), request.description());
        return channelMapper.toResponse(channelRepository.save(channel));
    }

    @Transactional
    @Override
    public ChannelResponse createPrivateChannel(PrivateChannelCreateRequest request) {
        Channel channel = new Channel(ChannelType.PRIVATE, null, null);

        for (UUID userId : request.participantIds()) {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));
            ReadStatus readStatus = new ReadStatus(user, channel, Instant.MIN);
            channel.getReadStatuses().add(readStatus);
        }

        Channel saved = channelRepository.save(channel);
        return channelMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public ChannelResponse find(UUID channelId) {
        return channelRepository.findById(channelId)
            .map(channelMapper::toResponse)
            .orElseThrow(() ->
                new NoSuchElementException("Channel with id " + channelId + " not found"));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ChannelResponse> findAllByUserId(UUID userId) {
        List<UUID> subscribedChannelIds = readStatusRepository.findAllByUser_Id(userId).stream()
            .map(readStatus -> readStatus.getChannel().getId())
            .toList();

        return channelRepository.findAll().stream()
            .filter(channel -> channel.getType() == ChannelType.PUBLIC
                || subscribedChannelIds.contains(channel.getId()))
            .map(channelMapper::toResponse)
            .toList();
    }


    @Transactional
    @Override
    public ChannelResponse update(UUID channelId, PublicChannelUpdateRequest request) {
        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(
                () -> new NoSuchElementException("Channel with id " + channelId + " not found"));

        if (channel.getType() == ChannelType.PRIVATE) {
            throw new IllegalArgumentException("Private channel cannot be updated");
        }

        channel.update(request.newName(), request.newDescription());
        return channelMapper.toResponse(channel);
    }


    @Transactional
    @Override
    public ChannelResponse delete(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> new NoSuchElementException("Channel not found"));

        channelRepository.delete(channel);
        return channelMapper.toResponse(channel);
    }
}
