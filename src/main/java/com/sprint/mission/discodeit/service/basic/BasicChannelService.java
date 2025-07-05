package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.ChannelException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;

  @Override
  public ChannelResponse create(String name, String description) {
    Channel channel = Channel.createPublic(name, description);
    Channel savedChannel = channelRepository.save(channel);
    return toResponse(savedChannel);
  }

  @Override
  public ChannelResponse create(List<UUID> participantIds) {
    Channel channel = Channel.createPrivate();
    for (UUID participantId : participantIds) {
      readStatusRepository.save(ReadStatus.create(participantId, channel.getId()));
    }
    Channel savedChannel = channelRepository.save(channel);
    return toResponse(savedChannel);
  }

  @Override
  public ChannelResponse findById(UUID channelId) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> ChannelException.notFound(channelId));
    return toResponse(channel);
  }

  @Override
  public List<ChannelResponse> findAllByUserId(UUID userId) {
    List<UUID> mySubscribedChannelIds = readStatusRepository.findAllByUserId(userId).stream()
        .map(ReadStatus::getChannelId)
        .toList();

    return channelRepository.findAll().stream()
        .filter(
            channel -> channel.getType() == ChannelType.PUBLIC || mySubscribedChannelIds.contains(
                channel.getId()))
        .map(this::toResponse)
        .collect(Collectors.toList());
  }

  @Override
  public ChannelResponse update(UUID channelId, String newName, String newDescription) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> ChannelException.notFound(channelId));

    if (channel.getType() == ChannelType.PRIVATE) {
      throw ChannelException.cannotUpdatePrivateChannel(channelId);
    }

    if (newName != null) {
      channel.updateName(newName);
    }

    if (newDescription != null) {
      channel.updateDescription(newDescription);
    }

    Channel updated = channelRepository.save(channel);
    return toResponse(updated);
  }


  @Override
  public ChannelResponse delete(UUID channelId) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> ChannelException.notFound(channelId));

    // 연관된 메시지 삭제
    messageRepository.findAll().stream()
        .filter(m -> m.getChannelId().equals(channelId))
        .forEach(m -> messageRepository.delete(m.getId()));

    // 연관된 읽음 상태 삭제
    readStatusRepository.findAllByChannelId(channelId)
        .forEach(rs -> readStatusRepository.delete(rs.getId()));

    // 채널 삭제
    channelRepository.delete(channelId);

    return toResponse(channel);
  }

  private ChannelResponse toResponse(Channel channel) {
    Instant lastMessageAt = messageRepository.findAll().stream()
        .filter(m -> m.getChannelId().equals(channel.getId()))
        .map(Message::getCreatedAt)
        .max(Comparator.naturalOrder())
        .orElse(null);

    List<UUID> participantIds = new ArrayList<>();
    if (channel.getType().equals(ChannelType.PRIVATE)) {
      readStatusRepository.findAllByChannelId(channel.getId())
          .stream()
          .map(ReadStatus::getUserId)
          .forEach(participantIds::add);
    }

    return new ChannelResponse(
        channel.getId(),
        channel.getType(),
        channel.getName(),
        channel.getDescription(),
        participantIds,
        lastMessageAt
    );
  }
}
