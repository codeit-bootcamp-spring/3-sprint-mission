package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelMapper channelMapper;

    @Transactional
    @Override
    public ChannelResponse createPublicChannel(PublicChannelCreateRequest request) {
        log.info("[BasicChannelService] Creating public channel. [name={}]", request.name());

        Channel channel = new Channel(ChannelType.PUBLIC, request.name(), request.description());
        channelRepository.save(channel);

        log.debug("[BasicChannelService] Public channel created. [id={}]", channel.getId());
        return channelMapper.toResponse(channel);
    }

    @Transactional
    @Override
    public ChannelResponse createPrivateChannel(PrivateChannelCreateRequest request) {
        log.info("[BasicChannelService] Creating private channel. [participants={}]",
            request.participantIds());

        Channel channel = new Channel(ChannelType.PRIVATE, null, null);
        channelRepository.save(channel);

        List<ReadStatus> readStatuses = userRepository.findAllById(request.participantIds())
            .stream()
            .map(user -> new ReadStatus(user, channel, channel.getCreatedAt()))
            .toList();
        readStatusRepository.saveAll(readStatuses);

        log.debug("[BasicChannelService] Private channel created. [id={}]", channel.getId());
        return channelMapper.toResponse(channel);
    }

    @Transactional(readOnly = true)
    @Override
    public ChannelResponse find(UUID channelId) {
        log.info("[BasicChannelService] Finding channel. [id={}]", channelId);

        return channelRepository.findById(channelId)
            .map(channel -> {
                log.debug("[BasicChannelService] Channel found. [id={}]", channelId);
                return channelMapper.toResponse(channel);
            })
            .orElseThrow(() -> {
                log.warn("[BasicChannelService] Channel not found. [id={}]", channelId);
                return new NoSuchElementException("Channel with id " + channelId + " not found");
            });
    }

    @Transactional(readOnly = true)
    @Override
    public List<ChannelResponse> findAllByUserId(UUID userId) {
        log.info("[BasicChannelService] Finding all channels for user. [userId={}]", userId);

        List<UUID> subscribedChannelIds = readStatusRepository.findAllByUserId(userId).stream()
            .map(ReadStatus::getChannel)
            .map(Channel::getId)
            .toList();

        List<ChannelResponse> result = channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC,
                subscribedChannelIds)
            .stream()
            .map(channelMapper::toResponse)
            .toList();

        log.debug("[BasicChannelService] Channels found. [count={}] [userId={}]", result.size(),
            userId);
        return result;
    }

    @Transactional
    @Override
    public ChannelResponse update(UUID channelId, PublicChannelUpdateRequest request) {
        log.info("[BasicChannelService] Updating channel. [id={}] [newName={}]", channelId,
            request.newName());

        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> {
                log.warn("[BasicChannelService] Channel not found for update. [id={}]", channelId);
                return new NoSuchElementException("Channel with id " + channelId + " not found");
            });

        if (channel.getType() == ChannelType.PRIVATE) {
            log.warn("[BasicChannelService] Cannot update private channel. [id={}]", channelId);
            throw new IllegalArgumentException("Private channel cannot be updated");
        }

        channel.update(request.newName(), request.newDescription());

        log.debug("[BasicChannelService] Channel updated. [id={}]", channelId);
        return channelMapper.toResponse(channel);
    }

    @Transactional
    @Override
    public ChannelResponse delete(UUID channelId) {
        log.info("[BasicChannelService] Deleting channel. [id={}]", channelId);

        if (!channelRepository.existsById(channelId)) {
            log.warn("[BasicChannelService] Channel not found for deletion. [id={}]", channelId);
            throw new NoSuchElementException("Channel with id " + channelId + " not found");
        }

        messageRepository.deleteAllByChannelId(channelId);
        readStatusRepository.deleteAllByChannelId(channelId);
        Channel channel = channelRepository.findById(channelId).get();
        channelRepository.deleteById(channelId);

        log.debug("[BasicChannelService] Channel deleted. [id={}]", channelId);
        return channelMapper.toResponse(channel);
    }
}
