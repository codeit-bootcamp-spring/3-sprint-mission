package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;

  @Override
  public ChannelResponse createPublicChannel(PublicChannelCreateRequest request) {
    Channel channel = new Channel(request.getName(), request.getDescription(), false);
    channelRepository.save(channel);
    return new ChannelResponse(channel.getId(), channel.getName(), channel.isPrivate());
  }

  @Override
  public ChannelResponse createPrivateChannel(PrivateChannelCreateRequest request) {
    Channel channel = new Channel("private", null, true);
    channelRepository.save(channel);

    for (UUID userId : request.getUserIds()) {
      ReadStatus readStatus = new ReadStatus(userId, channel.getId(), Instant.now());
      readStatusRepository.save(readStatus);
    }

    return new ChannelResponse(channel.getId(), channel.getName(), channel.isPrivate());
  }

  @Override
  public Channel create(String name) {
    Channel channel = new Channel(name);
    channelRepository.save(channel);
    return channel;
  }

  @Override
  public ChannelResponse findById(UUID id) {
    Channel channel = channelRepository.findById(id);
    if (channel == null) {
      return null;
    }

    Instant lastMessageAt = messageRepository.findAll().stream()
        .filter(m -> m.getChannelId().equals(id))
        .map(Message::getCreatedAt)
        .max(Instant::compareTo)
        .orElse(null);

    List<UUID> participantIds = null;
    if (channel.isPrivate()) {
      participantIds = readStatusRepository.findAllByChannelId(id).stream()
          .map(ReadStatus::getUserId)
          .toList();
    }

    return new ChannelResponse(
        channel.getId(),
        channel.getName(),
        channel.isPrivate(),
        lastMessageAt,
        participantIds
    );
  }

  @Override
  public List<ChannelResponse> findAll() {
    return channelRepository.findAll().stream()
        .filter(channel -> !channel.isPrivate())
        .map(this::toResponse)
        .collect(Collectors.toList());
  }

  @Override
  public List<ChannelResponse> findAllByUserId(UUID userId) {
    return channelRepository.findAll().stream()
        .filter(channel -> !channel.isPrivate() ||
            readStatusRepository.findAllByChannelId(channel.getId()).stream()
                .anyMatch(rs -> rs.getUserId().equals(userId)))
        .map(this::toResponse)
        .collect(Collectors.toList());
  }

  private ChannelResponse toResponse(Channel channel) {
    Instant lastMessageAt = messageRepository.findAll().stream()
        .filter(m -> m.getChannelId().equals(channel.getId()))
        .map(Message::getCreatedAt)
        .max(Instant::compareTo)
        .orElse(null);

    List<UUID> participantIds = null;
    if (channel.isPrivate()) {
      participantIds = readStatusRepository.findAllByChannelId(channel.getId()).stream()
          .map(ReadStatus::getUserId)
          .toList();
    }

    return new ChannelResponse(
        channel.getId(),
        channel.getName(),
        channel.isPrivate(),
        lastMessageAt,
        participantIds
    );
  }

  @Override
  public ChannelResponse update(PublicChannelUpdateRequest request) {
    Channel channel = channelRepository.findById(request.getChannelId());
    if (channel == null) {
      return null;
    }
    if (channel.isPrivate()) {
      throw new UnsupportedOperationException("Private channels cannot be updated.");
    }

    channel.update(request.getName(), request.getDescription());
    channelRepository.save(channel);
    return toResponse(channel);
  }

  @Override
  public Channel delete(UUID id) {
    Channel channel = channelRepository.findById(id);
    if (channel != null) {
      messageRepository.findAll().stream()
          .filter(m -> m.getChannelId().equals(id))
          .map(Message::getId)
          .forEach(messageRepository::delete);

      readStatusRepository.deleteByChannelId(id);
      
      channelRepository.delete(id);
    }
    return channel;
  }
}
