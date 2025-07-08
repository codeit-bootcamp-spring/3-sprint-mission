package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.time.Instant;
import java.util.*;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelMapper channelMapper;

    @Override
    public ChannelDto create(PrivateChannelCreateRequest request) {
        log.debug("Private Channel 생성 시작 : {}", request);
        // PRIVATE CHANNEL 생성
        Channel channel = new Channel(
            ChannelType.PRIVATE,
            // name 및 description 속성 생략
            null,
            null
        );
        Channel createdChannel = channelRepository.save(channel);

        Instant createdAt = createdChannel.getCreatedAt();

        for (UUID userId : request.participantIds()) {
            User user = userRepository.findById(userId)
                .orElseThrow(
                    () -> new NoSuchElementException("User with id " + userId + " not found"));

            ReadStatus readStatus = new ReadStatus(user, createdChannel, createdAt);
            readStatusRepository.save(readStatus);
        }

        log.info("Private Channel 생성 완료 : id = {}, name = {}", channel.getId(), channel.getName());
        return channelMapper.toDto(createdChannel);
    }

    @Override
    public ChannelDto create(PublicChannelCreateRequest request) {
        log.debug("Public Channel 생성 시작 : {}", request);

        String name = request.name();
        String description = request.description();
        // PUBLIC CHANNEL 생성
        Channel channel = new Channel(
            ChannelType.PUBLIC,
            name,
            description
        );
        Channel created = channelRepository.save(channel);

        log.info("Public Channel 생성 완료 : id = {}, name = {}", channel.getId(), channel.getName());
        return channelMapper.toDto(created);
    }

    @Override
    @Transactional(readOnly = true)
    public ChannelDto find(UUID channelId) {
        return channelRepository.findById(channelId)
            .map(channelMapper::toDto)
            .orElseThrow(() -> ChannelNotFoundException.withId(channelId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChannelDto> findAllByUserId(UUID userId) {
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
    public ChannelDto update(UUID channelId, PublicChannelUpdateRequest request) {
        log.debug("Public Channel 수정 시작 : id = {}, request = {}", channelId, request);
        String newName = request.newName();
        String newDescription = request.newDescription();
        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(
                () -> ChannelNotFoundException.withId(channelId));
        if (channel.getType().equals(ChannelType.PRIVATE)) {
            throw PrivateChannelUpdateException.forChannel(channelId);
        }
        channel.update(newName, newDescription);
        log.info("Public Channel 수정 완료 : id = {}, name = {}", channelId, channel.getName());
        return channelMapper.toDto(channel);
    }

    @Override
    public void delete(UUID channelId) {
        log.debug("채널 삭제 시작 : id = {}", channelId);
        if (!channelRepository.existsById(channelId)) {
            throw ChannelNotFoundException.withId(channelId);
        }

        messageRepository.deleteAllByChannelId(channelId);
        readStatusRepository.deleteAllByChannelId(channelId);

        channelRepository.deleteById(channelId);
        log.info("채널 삭제 완료 : id = {}", channelId);
    }

}

