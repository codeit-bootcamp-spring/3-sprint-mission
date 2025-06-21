package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService {

  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;
  private final ChannelMapper channelMapper;


  // Private 채널 생성
  @Override
  @Transactional
  public ChannelDto create(PrivateChannelCreateRequest channelCreateDto) {
    List<UUID> participantIds = channelCreateDto.participantIds();

    Channel channel =
        Channel.builder()
            .name(null)
            .description(null)
            .type(ChannelType.PRIVATE)
            .build();

    channelRepository.save(channel);

    // 참여자들의 메시지 수신 정보 생성
    List<ReadStatus> readStatuses = userRepository.findAllById(participantIds)
        .stream()
        .map(user -> ReadStatus.builder()
            .user(user)
            .channel(channel)
            .lastReadAt(channel.getCreatedAt())
            .build())
        .toList();
    readStatusRepository.saveAll(readStatuses);

    return channelMapper.toDto(channel);
  }

  // Public 채널 생성
  @Override
  @Transactional
  public ChannelDto create(PublicChannelCreateRequest channelCreateDto) {
    String name = channelCreateDto.name();
    String description = channelCreateDto.description();

    Channel channel =
        Channel.builder()
            .name(name)
            .description(description)
            .type(ChannelType.PUBLIC)
            .build();

    channelRepository.save(channel);

    return channelMapper.toDto(channel);
  }

  @Override
  @Transactional(readOnly = true)
  public ChannelDto find(UUID id) {

    return channelRepository.findById(id)
        .map(channelMapper::toDto)
        .orElseThrow(() -> new NoSuchElementException("해당 채널이 존재하지 않습니다."));
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
        .orElseThrow(() -> new NoSuchElementException("해당 채널이 존재하지 않습니다."));

    if (channel.getType().equals(ChannelType.PRIVATE)) {
      throw new IllegalArgumentException("Private 채널은 수정할 수 없습니다.");
    }

    channel.update(newName, newDescription);

    return channelMapper.toDto(channel);
  }

  @Override
  @Transactional
  public void delete(UUID id) {
    Channel channel = channelRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("해당 채팅방이 존재하지 않습니다."));

    messageRepository.deleteAllByChannelId(channel.getId());
    readStatusRepository.deleteAllByChannelId(channel.getId());

    channelRepository.deleteById(id);
  }

}
