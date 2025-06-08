package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ChannelDTO;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

@RequiredArgsConstructor
@Service
@Transactional
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;
  private final ChannelMapper channelMapper;


  // Private 채널 생성
  @Override
  public Channel create(PrivateChannelCreateRequest channelCreateDTO) {
    List<User> participants = channelCreateDTO.participants();

    System.out.println("participantIds = " + participants);

    Channel channel =
        Channel.builder()
            .name(null)
            .description(null)
            .type(ChannelType.PRIVATE)
            .build();

    channelRepository.save(channel);

    // 참여자들의 메시지 수신 정보 생성
    participants.stream()
        .map(user ->
            ReadStatus.builder()
                .user(user)
                .channel(channel)
                .lastReadAt(channel.getCreatedAt())
                .build())
        .forEach(readStatusRepository::save);

    return channel;
  }

  // Public 채널 생성
  @Override
  public Channel create(PublicChannelCreateRequest channelCreateDTO) {
    String name = channelCreateDTO.name();
    String description = channelCreateDTO.description();

    Channel channel =
        Channel.builder()
            .name(name)
            .description(description)
            .type(ChannelType.PUBLIC)
            .build();

    channelRepository.save(channel);

    return channel;
  }

  @Override
  @Transactional(readOnly = true)
  public ChannelDTO find(UUID id) {

    return channelRepository.findById(id)
        .map(channelMapper::toDTO)
        .orElseThrow(() -> new NoSuchElementException("해당 채널이 존재하지 않습니다."));
  }

  @Override
  @Transactional(readOnly = true)
  public List<ChannelDTO> findByName(String name) {

    return channelRepository.findByName(name).stream()
        .map(channelMapper::toDTO)
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public List<ChannelDTO> findAll() {

    return channelRepository.findAll().stream()
        .map(channelMapper::toDTO)
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public List<ChannelDTO> findAllByUserId(UUID userId) {
    List<Channel> mySubscribedChannels =
        readStatusRepository.findAllByUserId(userId).stream()
            .map(ReadStatus::getChannel)
            .toList();

    return channelRepository.findAll().stream()
        .filter(channel ->
            channel.getType().equals(ChannelType.PUBLIC)
                || mySubscribedChannels.contains(channel)
        )
        .map(channelMapper::toDTO)
        .toList();
  }

  @Override
  public Channel update(UUID id, PublicChannelUpdateRequest request) {
    String newName = request.newName();
    String newDescription = request.newDescription();

    Channel channel = channelRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("해당 채널이 존재하지 않습니다."));

    if (channel.getType().equals(ChannelType.PRIVATE)) {
      throw new IllegalArgumentException("Private 채널은 수정할 수 없습니다.");
    }

    channel.update(newName, newDescription);

    return channel;
  }

  @Override
  public void delete(UUID id) {
    Channel channel = channelRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("해당 채팅방이 존재하지 않습니다."));

    messageRepository.deleteAllByChannelId(channel.getId());
    readStatusRepository.deleteAllByChannelId(channel.getId());

    channelRepository.deleteById(id);
  }

}
