package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;

  @Override
  @Transactional(readOnly = true)
  public ChannelDto find(UUID channelId) {
    Channel c = channelRepository.findById(channelId)
        .orElseThrow(() -> new NoSuchElementException("Channel not found: " + channelId));

    List<UUID> participants = readStatusRepository
        .findAllByChannelId(channelId)
        .stream()
        .map(ReadStatus::getUserId)
        .collect(Collectors.toList());

    Instant lastMsgAt = messageRepository
        .findAllByChannelId(channelId)
        .stream()
        .map(m -> m.getCreatedAt())
        .max(Instant::compareTo)
        .orElse(null);

    return new ChannelDto(
        c.getId(),
        c.getType(),
        c.getName(),
        c.getDescription(),
        participants,
        lastMsgAt
    );
  }

  @Override
  @Transactional
  public Channel create(PublicChannelCreateRequest request) {
    Channel c = new Channel(
        ChannelType.PUBLIC,
        request.name(),
        request.description()
    );
    return channelRepository.save(c);
  }

  @Override
  @Transactional
  public Channel create(PrivateChannelCreateRequest request) {
    Channel c = new Channel(
        ChannelType.PRIVATE,
        null,
        null
    );
    Channel saved = channelRepository.save(c);
    request.participantIds().forEach(userId -> {
      ReadStatus rs = new ReadStatus(userId, saved.getId(), Instant.now());
      readStatusRepository.save(rs);
    });
    return saved;
  }

  @Override
  @Transactional(readOnly = true)
  public List<ChannelDto> findAllByUserId(UUID userId) {
    return readStatusRepository.findAllByUserId(userId)
        .stream()
        .map(rs -> find(rs.getChannelId()))
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public Channel update(UUID channelId, PublicChannelUpdateRequest request) {
    Channel c = channelRepository.findById(channelId)
        .orElseThrow(() -> new NoSuchElementException("Channel not found: " + channelId));
    c.update(request.newName(), request.newDescription());
    return channelRepository.save(c);
  }

  @Override
  @Transactional
  public void delete(UUID channelId) {
    if (!channelRepository.existsById(channelId)) {
      throw new NoSuchElementException("Channel not found: " + channelId);
    }
    channelRepository.deleteById(channelId);
  }
}
