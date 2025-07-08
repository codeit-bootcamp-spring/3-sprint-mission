package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.Common.ResourceNotFoundException;
import com.sprint.mission.discodeit.exception.ReadStatus.ReadStatusAlreadyExistsException;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicReadStatusService implements ReadStatusService {

  private final ReadStatusRepository readStatusRepository;
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final ReadStatusMapper readStatusMapper;


  @Transactional
  @Override
  public ReadStatusDto create(ReadStatusCreateRequest createRequest) {
    // 1. `Channel`이나`User`가 존재하지 않으면 예외 발생
    User user = this.userRepository.findById(createRequest.userId()).orElseThrow(
        () -> new ResourceNotFoundException("userId = " + createRequest.userId()));
    Channel channel = this.channelRepository.findById(createRequest.channelId()).orElseThrow(
        () -> new ResourceNotFoundException("channelId() = " + createRequest.channelId()));

    //2. 같은`Channel`과`User`와 관련된 객체가 이미 존재하면 예외를 발생
    if (readStatusRepository.existsByUserIdAndChannelId(createRequest.userId(), channel.getId())) {
      throw new ReadStatusAlreadyExistsException();
    }

    // 3. ReadStatus 생성
    ReadStatus readStatus = new ReadStatus(user, channel,
        createRequest.lastReadAt());

    //4. DB저장
    ReadStatus createdReadStatus = this.readStatusRepository.save(readStatus);

    return readStatusMapper.toDto(createdReadStatus);
  }

  @Override
  public ReadStatusDto find(UUID readStatusId) {
    return this.readStatusRepository
        .findById(readStatusId)
        .map(readStatusMapper::toDto)
        .orElseThrow(
            () -> new ResourceNotFoundException("readStatusId = " + readStatusId));
  }

  @Override
  public List<ReadStatusDto> findAllByUserId(UUID userId) {
    return this.readStatusRepository.findAllByUserId(userId)
        .stream()
        .map(readStatusMapper::toDto)
        .toList();
  }

  @Transactional
  @Override
  public ReadStatusDto update(UUID readStatusId, ReadStatusUpdateRequest updateRequest) {
    ReadStatus readStatus = this.readStatusRepository
        .findById(readStatusId)
        .orElseThrow(
            () -> new ResourceNotFoundException("readStatusId = " + readStatusId));

    readStatus.update(updateRequest.newLastReadAt());

    return this.readStatusMapper.toDto(readStatus);
  }

  @Transactional
  @Override
  public void delete(UUID readStatusId) {
    if (!readStatusRepository.existsById(readStatusId)) {
      throw new ResourceNotFoundException("readStatusId = " + readStatusId);
    }

    this.readStatusRepository.deleteById(readStatusId);
  }
}
