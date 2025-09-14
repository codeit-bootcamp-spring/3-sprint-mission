package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.ChannelUpdateEvent;
import com.sprint.mission.discodeit.event.ChannelUpdateType;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService {

    //레포지토리 의존성
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelMapper channelMapper;
    private final CacheManager cacheManager;
    private final ApplicationEventPublisher eventPublisher;

    // 공개 채널 생성
    @CacheEvict(cacheNames = "channelsByUser", allEntries = true)
    @PreAuthorize("hasRole('CHANNEL_MANAGER')")
    @Override
    @Transactional
    public ChannelDto createPublicChannel(PublicChannelCreateRequest request) {

        Channel channel = new Channel(
                request.name(),
                ChannelType.PUBLIC,
                request.description()
        );

        channelRepository.save(channel);
        ChannelDto dto = channelMapper.toDto(channel);

        eventPublisher.publishEvent(new ChannelUpdateEvent(ChannelUpdateType.CREATED, dto));

        return dto;
    }

    // 비공개 채널 생성
    @Override
    @Transactional
    public ChannelDto createPrivateChannel(PrivateChannelCreateRequest request) {

        Channel channel = new Channel(
                null,
                ChannelType.PRIVATE,
                null
        );
        channelRepository.save(channel);

        List<User> users = userRepository.findAllById(request.participantIds());

        List<ReadStatus> readStatuses = users.stream()
                .map(user -> new ReadStatus(user, channel, channel.getCreatedAt()))
                .toList();
        readStatusRepository.saveAll(readStatuses);

        // 참여자 캐시 무효화
        evictChannelUser(request.participantIds());
        ChannelDto dto = channelMapper.toDto(channel);

        eventPublisher.publishEvent(new ChannelUpdateEvent(ChannelUpdateType.CREATED, dto));

        return dto;
    }

    // 채널 공개 여부 별로 조건 달아준 전체 조회
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "channelsByUser", key = "#userId")
    public List<ChannelDto> findAllByUserId(UUID userId) {

        List<UUID> participants = readStatusRepository.findAllByUserId(userId).stream()
                .map(readStatus -> readStatus.getChannel().getId())
                .toList();

        return channelRepository.findAll().stream()
                .filter(channel ->
                        channel.getType().equals(ChannelType.PUBLIC)
                                || participants.contains(channel.getId())
                )
                .map(channelMapper::toDto)
                .toList();
    }

    // id로 채널 조회
    @Override
    @Transactional(readOnly = true)
    public ChannelDto find(UUID channelId) {
        return channelRepository.findById(channelId)
                .map(channelMapper::toDto)
                .orElseThrow(
                        () -> new ChannelNotFoundException(channelId));
    }

    // 채널 이름 수정
    @CacheEvict(cacheNames = "channelsByUser", allEntries = true)
    @PreAuthorize("hasRole('CHANNEL_MANAGER')")
    @Override
    @Transactional
    public ChannelDto update(UUID channelId, PublicChannelUpdateRequest request) {

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ChannelNotFoundException(channelId));

        if (channel.getType().equals(ChannelType.PRIVATE)) {
            throw new PrivateChannelUpdateException(channelId);
        }

        channel.updateChannelName(request.newName());
        channel.updateDescription(request.newDescription());
        log.info("ChannelService: 채널 이름과 설명 수정이 완료되었습니다.");

        ChannelDto dto = channelMapper.toDto(channel);

        eventPublisher.publishEvent(new ChannelUpdateEvent(ChannelUpdateType.UPDATED, dto));

        return dto;
    }

    // 채널 삭제
    @CacheEvict(cacheNames = "channelsByUser", allEntries = true)
    @PreAuthorize("hasRole('CHANNEL_MANAGER')")
    @Override
    @Transactional
    public void delete(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(
                        () -> new ChannelNotFoundException(channelId));

        List<Message> messages = messageRepository.findAllByChannelId(channelId);
        messageRepository.deleteAll(messages);

        List<ReadStatus> readStatuses = readStatusRepository.findAllByChannelId(channelId);
        readStatusRepository.deleteAll(readStatuses);

        ChannelDto dto = channelMapper.toDto(channel);

        eventPublisher.publishEvent(new ChannelUpdateEvent(ChannelUpdateType.DELETED, dto));

        channelRepository.deleteById(channelId);
        log.info("ChannelService: 채널이 삭제되었습니다.");
    }

    // 비공개 채널 참여자의 캐시 삭제를 위한 메서드
    private void evictChannelUser(List<UUID> userIds) {
        Cache cache = cacheManager.getCache("channelsByUser");

        if (cache != null) {
            for (UUID userId : userIds) {
                cache.evict(userId);
            }
        }
        else {
            log.warn("채널 캐시가 존재하지 않습니다.");
        }
    }
}
