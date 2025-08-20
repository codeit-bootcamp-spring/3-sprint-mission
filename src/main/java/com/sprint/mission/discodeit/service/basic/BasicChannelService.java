package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.base.BaseEntity;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService {

    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final ChannelMapper channelMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional
    @PreAuthorize("hasRole('CHANNEL_MANAGER')")
    public ChannelDto create(@Valid PublicChannelCreateRequest request) {
        String name = request.name();
        String description = request.description();
        Channel channel = new Channel(ChannelType.PUBLIC, name, description);
        log.debug("채널 entity 생성: {}", channel);

        channelRepository.save(channel);
        return channelMapper.toDto(channel);
    }

    @Override
    @Transactional
    public ChannelDto create(@Valid PrivateChannelCreateRequest request) {
        Channel channel = new Channel(ChannelType.PRIVATE, null, null);
        channelRepository.save(channel);
        log.debug("채널 entity 생성: {}", channel);

        List<ReadStatus> readStatuses = userRepository.findAllById(request.participantIds()).stream()
            .map(user -> new ReadStatus(user, channel, channel.getCreatedAt()))
            .toList();
        readStatusRepository.saveAll(readStatuses);

        return channelMapper.toDto(channel);
    }

    @Override
    public ChannelDto find(@NotNull UUID channelId) {
        return channelRepository.findById(channelId)
            .map(this::toDto)
            .orElseThrow(() -> {
                log.error("채널 조회 실패 - channelId={}", channelId);
                return new ChannelNotFoundException(channelId);
            });
    }

    @Override
    public List<ChannelDto> findAllByUserId(@NotNull UUID userId) {
        List<UUID> mySubscribedChannelIds = readStatusRepository.findAllByUserId(userId).stream()
            .map(ReadStatus::getChannel)
            .map(Channel::getId)
            .toList();

        return channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC, mySubscribedChannelIds)
            .stream()
            .map(channelMapper::toDto)
            .toList();
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('CHANNEL_MANAGER')")
    public ChannelDto update(@NotNull UUID channelId, @Valid PublicChannelUpdateRequest request) {
        String newName = request.newName();
        String newDescription = request.newDescription();

        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> {
                log.error("채널 조회 실패 - channelId={}", channelId);
                return new ChannelNotFoundException(channelId);
            });

        if (channel.getType().equals(ChannelType.PRIVATE)) {
            throw new PrivateChannelUpdateException(channel.getType());
        }

        channel.update(newName, newDescription);
        return channelMapper.toDto(channel);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('CHANNEL_MANAGER')")
    public void delete(@NotNull UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> {
                log.error("채널 조회 실패 - channelId={}", channelId);
                return new ChannelNotFoundException(channelId);
            });

        channelRepository.delete(channel);
    }

    private ChannelDto toDto(Channel channel) {
        Instant lastMessageAt = channel.getMessages().stream()
            .map(BaseEntity::getCreatedAt)
            .max(Instant::compareTo)
            .orElse(Instant.MIN);

        List<UserDto> participants = channel.getReadStatuses().stream()
            .map(readStatus -> userMapper.toDto(readStatus.getUser()))
            .toList();

        return new ChannelDto(
            channel.getId(),
            channel.getType(),
            channel.getName(),
            channel.getDescription(),
            participants,
            lastMessageAt
        );
    }
}
