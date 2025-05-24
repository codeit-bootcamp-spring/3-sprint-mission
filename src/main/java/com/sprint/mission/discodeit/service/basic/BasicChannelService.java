package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ChannelDTO;
import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.time.Instant;
import java.util.*;


@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;

  // 리펙토링


  @Override
  public Channel create(PrivateChannelCreateRequest privateChannelCreateRequest) {
    // PRIVATE CHANNEL 생성
    Channel privateChannel = new Channel(
        ChannelType.PRIVATE,
        // name 및 description 속성 생략
        null,
        null
    );
    channelRepository.save(privateChannel);

    // 참여한 User 별 활동상태 여부 받기
    for (UUID userId : privateChannelCreateRequest.getParticipantIds()) {
      User user = userRepository.findById(userId)
          .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

      ReadStatus readStatus = new ReadStatus(
          user.getUserId(),
          privateChannel.getChannelId(),
          // 가장 작은 시간 값 : LastReadAt
          Instant.MIN

      );
      readStatusRepository.save(readStatus);
    }

    return privateChannel;
  }

  @Override
  public Channel create(PublicChannelCreateRequest publicChannelCreateRequest) {

    // PUBLIC CHANNEL 생성
    Channel publicChannel = new Channel(
        ChannelType.PUBLIC,
        publicChannelCreateRequest.getName(),
        publicChannelCreateRequest.getDescription()
    );
    channelRepository.save(publicChannel);

    return publicChannel;
  }

  @Override
  public ChannelDTO find(UUID id) {
    Channel channel = channelRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("Channel with id " + id + " not found"));
    return toDto(channel);
  }

  @Override
  public List<ChannelDTO> findAllByUserId(UUID userId) {
    List<UUID> mySubscribedChannelIds = readStatusRepository.findAllByUserId(userId).stream()
        .map(ReadStatus::getChannelId)
        .toList();

    return channelRepository.findAll().stream()
        .filter(channel ->
            channel.getChannelType().equals(ChannelType.PUBLIC)
                || mySubscribedChannelIds.contains(channel.getChannelId())
        )
        .map(this::toDto)
        .toList();
  }

  @Override
  public Channel update(UUID channelId, ChannelUpdateRequest channelUpdateRequest) {
    // 유효성
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(
            () -> new NoSuchElementException("Channel with id " + channelId + " not found"));

    // PRIVATE CHANNEL : 수정 금지
    if (channel.getChannelType() == ChannelType.PRIVATE) {
      throw new UnsupportedOperationException("PRIVATE CHANNEL은 수정할 수 없습니다");
    }

    // PUBLIC CHANNEL : 수정 적용 가능
    channel.update(
        channelUpdateRequest.getNewName(),
        channelUpdateRequest.getNewDescription()
    );

    return channelRepository.save(channel);
  }

  @Override
  public void delete(UUID channelId) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(
            () -> new NoSuchElementException("Channel with id " + channelId + " not found"));

    messageRepository.deleteAllByChannelId(channel.getChannelId());
    readStatusRepository.deleteAllByChannelId(channel.getChannelId());

    channelRepository.deleteById(channelId);
  }

  private ChannelDTO toDto(Channel channel) {
    return new ChannelDTO(
        channel.getChannelId(),
        channel.getChannelType(),
        channel.getChannelName(),
        channel.getDescription(),
        List.of(),
        null
    );
  }
}

