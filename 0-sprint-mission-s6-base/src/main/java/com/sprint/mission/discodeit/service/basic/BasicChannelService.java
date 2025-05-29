package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ChannelRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusRequest;
import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.ReadStatusService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final ChannelMapper channelMapper;
  private final ReadStatusService readStatusService;
  private final MessageRepository messageRepository;
  private final UserRepository userRepository;


  @Override
  @Transactional
  public ChannelResponse create(ChannelRequest.CreatePublic request) {
    String name = request.name();
    String description = request.description();
    Channel channel = new Channel(Channel.ChannelType.PUBLIC, name, description);
    channelRepository.save(channel);

    log.info("Public 채널 생성 : {}", channel);

    return channelMapper.entityToDto(channel);
  }

  @Override
  @Transactional
  public ChannelResponse create(ChannelRequest.CreatePrivate request) {
    Channel channel = new Channel(Channel.ChannelType.PRIVATE, null, null);
    Channel createdChannel = channelRepository.save(channel);

    for (UUID userId : request.participantIds()) {
      readStatusService.create(
          new ReadStatusRequest.Create(userId, createdChannel.getId(),
              createdChannel.getCreatedAt()));
    }

    log.info("Private 채널 생성 : {}", createdChannel);
    return channelMapper.entityToDto(createdChannel);
  }

  @Override
  public ChannelResponse find(UUID id) {
    return channelRepository.findById(id)
        .map(channelMapper::entityToDto)
        .orElseThrow(
            () -> new NoSuchElementException("Channel with id " + id + " not found"));
  }

  @Override
  public List<ChannelResponse> findAllByUserId(UUID userId) {
    User user = userRepository.findById(userId).orElseThrow(() ->
        new NoSuchElementException("User with id " + userId + " not found"));

    return channelRepository.findAllByTypeOrUserId(userId, Channel.ChannelType.PUBLIC).stream()
        .map(channelMapper::entityToDto)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public ChannelResponse update(UUID id, ChannelRequest.Update request) {
    String newName = request.name();
    String newDescription = request.description();

    Channel channel = channelRepository.findById(id)
        .orElseThrow(
            () -> new NoSuchElementException("Channel with id " + id + " not found"));

    if (channel.getType() == Channel.ChannelType.PRIVATE) {
      throw new IllegalArgumentException("Private channel cannot be updated");
    }
    Optional.ofNullable(newName).ifPresent(channel::updateName);
    Optional.ofNullable(newDescription).ifPresent(channel::updateDescription);

    log.info("수정된 채널 : {}", channel);
    return channelMapper.entityToDto(channelRepository.save(channel));
  }

  @Override
  public void delete(UUID id) {
    Channel channel = channelRepository.findById(id)
        .orElseThrow(
            () -> new NoSuchElementException("Channel with id " + id + " not found"));

    channelRepository.deleteById(id);
  }


}
