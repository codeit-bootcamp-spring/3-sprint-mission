package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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

    @Override
    @Transactional
    @PreAuthorize("hasRole('CHANNEL_MANAGER')")
    @CacheEvict(value = {"channelsByUser", "channel"}, allEntries = true)
    public ChannelDto create(@Valid PublicChannelCreateRequest request) {
        String name = request.name();
        String description = request.description();
        Channel channel = new Channel(ChannelType.PUBLIC, name, description);
        channelRepository.save(channel);

        log.debug("채널 entity 생성 및 DB에 저장 완료: {}", channel);

        List<User> users = userRepository.findAll();
        List<ReadStatus> readStatuses = users.stream()
            .map(user -> new ReadStatus(user, channel, channel.getCreatedAt(), false))
            .toList();
        readStatusRepository.saveAll(readStatuses);

        log.debug("저장된 ReadStatus 수: {}", readStatuses.size());

        return channelMapper.toDto(channel);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"channelsByUser", "channel"}, allEntries = true)
    public ChannelDto create(@Valid PrivateChannelCreateRequest request) {
        Channel channel = new Channel(ChannelType.PRIVATE, null, null);
        channelRepository.save(channel);
        log.debug("채널 entity 생성: {}", channel);

        List<ReadStatus> readStatuses = userRepository.findAllById(request.participantIds()).stream()
            .map(user -> new ReadStatus(user, channel, channel.getCreatedAt(), true))
            .toList();
        readStatusRepository.saveAll(readStatuses);

        return channelMapper.toDto(channel);
    }

    @Override
    @Cacheable(value = "channel", key = "#channelId")
    public ChannelDto find(@NotNull UUID channelId) {
        return channelRepository.findById(channelId)
            .map(channelMapper::toDto)
            .orElseThrow(() -> {
                log.error("채널 조회 실패 - channelId={}", channelId);
                return new ChannelNotFoundException(channelId);
            });
    }


    @Override
    @Cacheable(value = "channelsByUser", key = "#userId")
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
    @CacheEvict(value = {"channelsByUser", "channel"}, allEntries = true)
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
    @CacheEvict(value = {"channelsByUser", "channel"}, allEntries = true)
    public void delete(@NotNull UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> {
                log.error("채널 조회 실패 - channelId={}", channelId);
                return new ChannelNotFoundException(channelId);
            });

        channelRepository.delete(channel);
    }
}
