package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class BasicChannelService implements ChannelService {

  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;
  private final ChannelMapper channelMapper;


  // 비공개 채널 생성
  @Override
  @Transactional
  public ChannelDto create(PrivateChannelCreateRequest channelCreateDto) {
    List<UUID> participantIds = channelCreateDto.participantIds();

    log.debug("비공개 채널 객체 생성 시작 request={}", channelCreateDto);
    Channel channel =
        Channel.builder()
            .name(null)
            .description(null)
            .type(ChannelType.PRIVATE)
            .build();
    log.debug("비공개 채널 객체 생성 완료 channelId={}", channel.getId());

    log.debug("[channelRepository] 비공개 채널 저장 시작 channelId={}", channel.getId());
    channelRepository.save(channel);
    log.debug("[channelRepository] 비공개 채널 저장 완료 channelId={}", channel.getId());

    // 참여자들의 메시지 수신 정보 생성
    log.debug("[readStatusRepository] 참여자들의 메시지 수신 정보 객체 생성 시작");
    List<ReadStatus> readStatuses = userRepository.findAllById(participantIds)
        .stream()
        .map(user -> ReadStatus.builder()
            .user(user)
            .channel(channel)
            .lastReadAt(channel.getCreatedAt())
            .build())
        .toList();
    log.debug("[readStatusRepository] 참여자들의 메시지 수신 정보 생성 완료 readStatuses={}", readStatuses);

    readStatusRepository.saveAll(readStatuses);
    log.debug("[readStatusRepository] 참여자들의 메시지 수신 정보 저장 완료 readStatuses={}", readStatuses);

    return channelMapper.toDto(channel);
  }

  // 공개 채널 생성
  @Override
  @Transactional
  public ChannelDto create(PublicChannelCreateRequest channelCreateDto) {
    log.debug("공개 채널 생성 요청 {}", channelCreateDto);
    String name = channelCreateDto.name();
    String description = channelCreateDto.description();

    log.debug("공개 채널 객체 생성 시작");
    Channel channel =
        Channel.builder()
            .name(name)
            .description(description)
            .type(ChannelType.PUBLIC)
            .build();
    log.debug("Public 채널 객체 생성 완료 channel={}", channel);

    channelRepository.save(channel);
    log.debug("[channelRepository] Public 채널 저장 완료 channel={}", channel);

    return channelMapper.toDto(channel);
  }

  @Override
  @Transactional(readOnly = true)
  public ChannelDto find(UUID id) {

    return channelRepository.findById(id)
        .map(channelMapper::toDto)
        .orElseThrow(() -> new ChannelNotFoundException(id));
  }

  @Override
  @Transactional(readOnly = true)
  public List<ChannelDto> findByName(String name) {

    return channelRepository.findByName(name).stream()
        .map(channelMapper::toDto)
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public List<ChannelDto> findAll() {

    return channelRepository.findAll().stream()
        .map(channelMapper::toDto)
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public List<ChannelDto> findAllByUserId(UUID userId) {
    List<UUID> mySubscribedChannelIds =
        readStatusRepository.findAllByUserId(userId).stream()
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
  public ChannelDto update(UUID id, PublicChannelUpdateRequest request) {
    String newName = request.newName();
    String newDescription = request.newDescription();

    Channel channel = channelRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("해당 채널이 존재하지 않습니다. channelId={}", id);
          return new ChannelNotFoundException(id);
        });

    if (channel.getType().equals(ChannelType.PRIVATE)) {
      log.warn("비공개 채널은 수정할 수 없습니다. channelId={}", id);
      throw new PrivateChannelUpdateException(id);
    }

    log.debug("[channelRepository] 공개 채널 수정 시작 channelId={}", channel.getId());
    channel.update(newName, newDescription);
    log.debug("[channelRepository] 공개 채널 수정 완료 channelId={}", channel.getId());

    return channelMapper.toDto(channel);
  }

  @Override
  @Transactional
  public void delete(UUID id) {
    log.debug("공개 채널 삭제 요청 {}", id);
    Channel channel = channelRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("해당 채널이 존재하지 않습니다. channelId={}", id);
          return new ChannelNotFoundException(id);
        });

    messageRepository.deleteAllByChannelId(channel.getId());
    log.debug("[messageRepository] 공개 채널 메시지 삭제 완료 channelId={}", channel.getId());

    readStatusRepository.deleteAllByChannelId(channel.getId());
    log.debug("[readStatusRepository] 공개 채널 메시지 수신 정보 삭제 완료 channelId={}", channel.getId());

    channelRepository.deleteById(id);
    log.debug("[channelRepository] 공개 채널 삭제 완료 channelId={}", channel.getId());
  }

}
