package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class BasicReadStatusService implements ReadStatusService {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final ReadStatusMapper readStatusMapper;

  @Override
  @Transactional
  public ReadStatusDto create(ReadStatusCreateRequest request) {
    UUID userId = request.userId();
    UUID channelId = request.channelId();

    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> {
          log.warn("해당 채널이 존재하지 않습니다. channelId={}", channelId);
          return new NoSuchElementException("해당 채널이 존재하지 않습니다.");
        });

    User user = userRepository.findById(userId)
        .orElseThrow(() -> {
          log.warn("해당 사용자가 존재하지 않습니다. userId={}", userId);
          return new NoSuchElementException("해당 사용자가 존재하지 않습니다.");
        });

    if (readStatusRepository.findAllByChannelId(channel.getId()).stream()
        .anyMatch(readStatus -> readStatus.getUser().equals(user))) {
      log.warn("이미 존재하는 데이터입니다. userId={}, channelId={}", userId, channelId);
    }

    Instant lastReadAt = request.lastReadAt();

    log.debug("ReadStatus 객체 생성 시작 request={}", request);
    ReadStatus readStatus =
        ReadStatus.builder()
            .user(user)
            .channel(channel)
            .lastReadAt(lastReadAt)
            .build();
    log.debug("ReadStatus 객체 생성 완료 userId={}, channelId={}", readStatus.getUser().getId(),
        readStatus.getChannel().getId());

    log.debug("[readStatusRepository] 데이터 저장 시작 readStatusId={}", readStatus.getId());
    readStatusRepository.save(readStatus);
    log.debug("[readStatusRepository] 데이터 저장 완료 readStatusId={}", readStatus.getId());

    return readStatusMapper.toDto(readStatus);
  }

  @Override
  @Transactional(readOnly = true)
  public ReadStatusDto find(UUID readStatusId) {
    return readStatusRepository.findById(readStatusId)
        .map(readStatusMapper::toDto)
        .orElseThrow(() -> new NoSuchElementException("데이터가 존재하지 않습니다."));
  }

  @Transactional(readOnly = true)
  public List<ReadStatusDto> findAll() {
    return readStatusRepository.findAll().stream()
        .map(readStatusMapper::toDto)
        .toList();

  }

  @Override
  @Transactional(readOnly = true)
  public List<ReadStatusDto> findAllByUserId(UUID userId) {
    findAll().forEach(System.out::println);

    return readStatusRepository.findAllByUserId(userId).stream()
        .map(readStatusMapper::toDto)
        .toList();
  }

  @Override
  @Transactional
  public ReadStatusDto update(UUID readStatusId, ReadStatusUpdateRequest request) {
    Instant newLastReadAt = request.lastReadAt();
    ReadStatus readStatus = readStatusRepository.findById(readStatusId)
        .orElseThrow(() -> {
          log.warn("데이터가 존재하지 않습니다. readStatusId={}", readStatusId);
          return new NoSuchElementException("데이터가 존재하지 않습니다.");
        });

    log.debug("[readStatusRepository] 데이터 수정 시작 readStatusId={}", readStatusId);
    readStatus.update(newLastReadAt);
    log.debug("[readStatusRepository] 데이터 수정 완료 readStatusId={}", readStatusId);

    return readStatusMapper.toDto(readStatus);
  }

  @Override
  @Transactional
  public void delete(UUID readStatusId) {
    if (!readStatusRepository.existsById(readStatusId)) {
      log.warn("데이터가 존재하지 않습니다. readStatusId={}", readStatusId);
    }

    log.debug("[readStatusRepository] 데이터 삭제 시작 readStatusId={}", readStatusId);
    readStatusRepository.deleteById(readStatusId);
    log.debug("[readStatusRepository] 데이터 삭제 완료 readStatusId={}", readStatusId);
  }
}
