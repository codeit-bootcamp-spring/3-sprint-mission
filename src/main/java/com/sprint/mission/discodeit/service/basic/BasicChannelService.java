package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.base.BaseEntity;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService {

  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final ChannelMapper channelMapper;
  private final UserMapper userMapper;

  @Override
  @Transactional
  public ChannelDto create(@Valid PublicChannelCreateRequest request) {
    String name = request.name();
    String description = request.description();
    Channel channel = new Channel(ChannelType.PUBLIC, name, description);
    log.info("채널 entity 생성: {}", channel);

    channelRepository.save(channel);
    return channelMapper.toDto(channel);
  }

  @Override
  @Transactional
  public ChannelDto create(@Valid PrivateChannelCreateRequest request) {
    Channel channel = new Channel(ChannelType.PRIVATE, null, null);
    log.info("채널 entity 생성: {}", channel);

    request.participantIds().stream()
        .map(userId -> userRepository.findById(userId)
            .orElseThrow(() -> {
              log.error("사용자 조회 실패 - userId={}", userId);
              return new NoSuchElementException("유효하지 않은 사용자 (userId=" + userId + ")");
            }))
        .map(user -> new ReadStatus(user, channel, channel.getCreatedAt()))
        .forEach(channel.getReadStatuses()::add);

    channelRepository.save(channel);
    return channelMapper.toDto(channel);
  }

  @Override
  public ChannelDto find(@NotNull UUID channelId) {
    return channelRepository.findById(channelId)
        .map(this::toDto)
        .orElseThrow(() -> {
          log.error("채널 조회 실패 - channelId={}", channelId);
          return new NoSuchElementException("유효하지 않은 채널 (channelId=" + channelId + ")");
        });
  }

  @Override
  public List<ChannelDto> findAllByUserId(@NotNull UUID userId) {
    List<Channel> channels = channelRepository
        .findAllAccessible(ChannelType.PUBLIC, userId);
    return channels.stream()
        .map(this::toDto)
        .toList();
  }

  @Override
  @Transactional
  public ChannelDto update(@NotNull UUID channelId, @Valid PublicChannelUpdateRequest request) {
    String newName = request.newName();
    String newDescription = request.newDescription();

    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> {
          log.error("채널 조회 실패 - channelId={}", channelId);
          return new NoSuchElementException("유효하지 않은 채널 (channelId=" + channelId + ")");
        });

    if (channel.getType().equals(ChannelType.PRIVATE)) {
      throw new IllegalArgumentException("수정이 불가능한 채널 (" + ChannelType.PRIVATE + ")");
    }

    channel.update(newName, newDescription);
    return channelMapper.toDto(channel);
  }

  @Override
  @Transactional
  public void delete(@NotNull UUID channelId) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> {
          log.error("채널 조회 실패 - channelId={}", channelId);
          return new NoSuchElementException("유효하지 않은 채널 (channelId=" + channelId + ")");
        });

    channelRepository.delete(channel);
  }

  private ChannelDto toDto(Channel channel) {
    Instant lastMessageAt = channel.getMessages().stream()
        .map(BaseEntity::getCreatedAt)
        .max(Instant::compareTo)
        .orElse(Instant.MIN);

    List<UserDto> participants = channel.getReadStatuses().stream()
        .map(readStatus -> userMapper.toDto(readStatus.getUser()))
        .toList();

    return new ChannelDto(
        channel.getId(),
        channel.getType(),
        channel.getName(),
        channel.getDescription(),
        participants,
        lastMessageAt
    );
  }
}
