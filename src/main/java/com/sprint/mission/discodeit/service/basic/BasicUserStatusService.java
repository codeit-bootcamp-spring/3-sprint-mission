package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.Common.ResourceNotFoundException;
import com.sprint.mission.discodeit.exception.UserStatus.UserStatusAlreadyExistsException;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

  private final UserStatusRepository userStatusRepository;
  private final UserRepository userRepository;
  private final UserStatusMapper userStatusMapper;

  @Transactional
  @Override
  public UserStatusDto create(UserStatusCreateRequest createRequest) {
    //1.  `User`가 존재하지 않으면 예외 발생
    User user = this.userRepository.findById(createRequest.userId()).orElseThrow(
        () -> new ResourceNotFoundException("UserId = " + createRequest.userId()));
    //2.  같은 `User`와 관련된 객체가 이미 존재하면 예외를 발생
    this.userStatusRepository.findById(createRequest.userId()).ifPresent((userStatus) -> {
      throw new UserStatusAlreadyExistsException(userStatus);
    });
    // 3. ReadStatus 생성
    UserStatus userStatus = new UserStatus(user, Instant.now());
    //4. DB저장
    this.userStatusRepository.save(userStatus);

    return userStatusMapper.toDto(userStatus);
  }

  @Override
  public UserStatusDto find(UUID userStatusId) {
    return this.userStatusRepository
        .findById(userStatusId)
        .map(userStatusMapper::toDto)
        .orElseThrow(
            () -> new ResourceNotFoundException("userStatusId = " + userStatusId));
  }

  @Override
  public List<UserStatusDto> findAll() {

    return this.userStatusRepository.findAll().stream()
        .map(userStatusMapper::toDto).toList();
  }

  @Transactional
  @Override
  public UserStatusDto update(UUID userStatusId, UserStatusUpdateRequest updateRequest) {
    UserStatus userStatus = this.userStatusRepository
        .findById(userStatusId)
        .orElseThrow(
            () -> new ResourceNotFoundException("userStatusId = " + userStatusId));
    userStatus.update(updateRequest.newLastActiveAt());

    return userStatusMapper.toDto(userStatus);
  }

  @Transactional
  @Override
  public UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest updateRequest) {
    UserStatus userStatus = this.userStatusRepository
        .findByUserId(userId)
        .orElseThrow(
            () -> new ResourceNotFoundException("userId = " + userId));
    userStatus.update(updateRequest.newLastActiveAt());

    return userStatusMapper.toDto(userStatus);
  }

  @Transactional
  @Override
  public void delete(UUID userStatusId) {
    if (!this.userStatusRepository.existsById(userStatusId)) {
      throw new ResourceNotFoundException("userStatusId = " + userStatusId);
    }
    this.userStatusRepository.deleteById(userStatusId);
  }
}
