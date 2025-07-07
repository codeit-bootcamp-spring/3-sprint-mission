package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.readStatus.DuplicateReadStatusException;
import com.sprint.mission.discodeit.exception.readStatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Validated
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BasicReadStatusService implements ReadStatusService {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final ReadStatusMapper readStatusMapper;

  @Override
  @Transactional
  public ReadStatusDto create(@Valid ReadStatusCreateRequest request) {
    UUID userId = request.userId();
    UUID channelId = request.channelId();

    User user = userRepository.findById(userId)
        .orElseThrow(() -> {
          log.error("사용자 조회 실패 - userId={}", userId);
          return new UserNotFoundException(userId);
        });
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> {
          log.error("채널 조회 실패 - channelId={}", channelId);
          return new ChannelNotFoundException(channelId);
        });

    if (readStatusRepository.findAllByUserId(userId).stream()
        .anyMatch(
            readStatus -> readStatus.getChannel().equals(channelRepository.findById(channelId)))) {
      log.error("이미 존재하는 읽음 상태 - userId={}, channelId={}", userId, channelId);
      throw new DuplicateReadStatusException(userId, channelId);
    }

    Instant lastReadAt = request.lastReadAt();
    ReadStatus readStatus = new ReadStatus(user, channel, lastReadAt);
    return readStatusMapper.toDto(readStatus);
  }

  @Override
  public ReadStatus find(@NotNull UUID readStatusId) {
    return readStatusRepository.findById(readStatusId)
        .orElseThrow(() -> {
          log.error("읽음 상태 조회 실패 - readStatusId={}", readStatusId);
          return new ReadStatusNotFoundException(readStatusId);
        });
  }

  @Override
  public List<ReadStatusDto> findAllByUserId(@NotNull UUID userId) {
    List<ReadStatus> readStatuses = readStatusRepository.findAllByUserId(userId);
    return readStatuses.stream()
        .map(readStatusMapper::toDto)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public ReadStatusDto update(@NotNull UUID readStatusId, @Valid ReadStatusUpdateRequest request) {
    Instant newLastReadAt = request.newLastReadAt();
    ReadStatus readStatus = readStatusRepository.findById(readStatusId)
        .orElseThrow(() -> {
          log.error("읽음 상태 조회 실패 - readStatusId={}", readStatusId);
          return new ReadStatusNotFoundException(readStatusId);
        });
    return readStatusMapper.toDto(readStatus);
  }

  @Override
  @Transactional
  public void delete(@NotNull UUID readStatusId) {
    ReadStatus readStatus = readStatusRepository.findById(readStatusId)
        .orElseThrow(() -> {
          log.error("읽음 상태 조회 실패 - readStatusId={}", readStatusId);
          return new ReadStatusNotFoundException(readStatusId);
        });
    readStatusRepository.delete(readStatus);
  }
}
