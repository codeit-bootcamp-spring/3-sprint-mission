package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.common.exception.ChannelException;
import com.sprint.mission.discodeit.dto.ChannelResponse;
import com.sprint.mission.discodeit.dto.data.PrivateChannelResponse;
import com.sprint.mission.discodeit.dto.data.PublicChannelResponse;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChannelServiceImpl implements ChannelService {

  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;

  @Override
  public Channel create(UUID creatorId, String name, String description) {
    Channel channel = Channel.create(creatorId, name, description);
    return channelRepository.save(channel);
  }

  @Override
  public Channel createPublic(PublicChannelCreateRequest dto) {
    Channel channel = Channel.createPublic(dto.creator(), dto.name(), dto.description());
    return channelRepository.save(channel);
  }

  @Override
  public Channel createPrivate(PrivateChannelCreateRequest dto) {
    Channel channel = Channel.createPrivate(dto.creator());
    for (UUID participantId : dto.participantIds()) {
      try {
        channel.addParticipant(participantId);
        readStatusRepository.save(ReadStatus.create(participantId, channel.getId()));
      } catch (ChannelException e) {
        throw new RuntimeException("Failed to add participant to private channel", e);
      }
    }
    return channelRepository.save(channel);
  }

  @Override
  public Optional<ChannelResponse> findById(UUID id) {
    return channelRepository.findById(id).map(this::toResponse);
  }

  @Override
  public List<ChannelResponse> findAllByUserId(UUID userId) {
    return channelRepository.findAll().stream()
        .filter(channel ->
            channel.getType() == Channel.ChannelType.PUBLIC ||
                channel.isParticipant(userId)
        )
        .map(this::toResponse)
        .collect(Collectors.toList());
  }

  @Override
  public List<ChannelResponse> findByCreatorIdOrName(UUID creatorId, String name) {
    return channelRepository.findAll().stream()
        .filter(channel -> channel.matchesFilter(creatorId, name))
        .map(this::toResponse)
        .collect(Collectors.toList());
  }

  @Override
  public Optional<ChannelResponse> update(PublicChannelUpdateRequest request) {
    return channelRepository.findById(request.channelId())
        .map(channel -> {
          if (channel.getType() == Channel.ChannelType.PRIVATE) {
            throw ChannelException.cannotUpdatePrivateChannel(request.channelId());
          }
          if (request.name() != null) {
            channel.updateName(request.name());
          }
          if (request.description() != null) {
            channel.updateDescription(request.description());
          }
          return channelRepository.save(channel);
        })
        .map(this::toResponse);
  }

  @Override
  public void addParticipant(UUID channelId, UUID userId) throws ChannelException {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> ChannelException.notFound(channelId));
    channel.addParticipant(userId);
    readStatusRepository.save(ReadStatus.create(userId, channelId));
    channelRepository.save(channel);
  }

  @Override
  public void removeParticipant(UUID channelId, UUID userId) throws ChannelException {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> ChannelException.notFound(channelId));
    channel.removeParticipant(userId);
    channelRepository.save(channel);
  }

  @Override
  public Optional<ChannelResponse> delete(UUID channelId) {
    Optional<Channel> deleted = channelRepository.findById(channelId);
    deleted.ifPresent(channel -> {
      // 연관된 메시지 삭제
      messageRepository.findAll().stream()
          .filter(m -> m.getChannelId().equals(channelId))
          .forEach(m -> messageRepository.delete(m.getId()));
      // 연관된 읽음 상태 삭제
      readStatusRepository.findByChannelId(channelId)
          .forEach(rs -> readStatusRepository.delete(rs.getId()));
      // 채널 삭제
      channelRepository.delete(channelId);
    });
    return deleted.map(this::toResponse);
  }

  private ChannelResponse toResponse(Channel channel) {
    Instant latestMessageTime = messageRepository.findAll().stream()
        .filter(m -> m.getChannelId().equals(channel.getId()))
        .map(Message::getCreatedAt)
        .max(Comparator.naturalOrder())
        .orElse(null);

    if (channel.getType() == Channel.ChannelType.PRIVATE) {
      return PrivateChannelResponse.from(channel, latestMessageTime);
    } else {
      return PublicChannelResponse.from(channel, latestMessageTime);
    }
  }
}
