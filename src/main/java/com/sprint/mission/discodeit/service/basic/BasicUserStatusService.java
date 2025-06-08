package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.UserStatusAlreadyExistsException;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicUserStatusService implements UserStatusService {

  private final UserStatusRepository userStatusRepository;
  private final UserRepository userRepository;
  private final UserStatusMapper userStatusMapper;

  @Override
  public UserStatusDto create(UserStatusCreateRequest createRequest) {
    //1.  `User`가 존재하지 않으면 예외 발생
    User user = this.userRepository.findById(createRequest.userId()).orElseThrow(
        () -> new NoSuchElementException("User with id " + createRequest.userId() + " not found"));

    //2.  같은 `User`와 관련된 객체가 이미 존재하면 예외를 발생
    this.userStatusRepository.findById(createRequest.userId()).ifPresent((userStatus) -> {
      throw new UserStatusAlreadyExistsException(userStatus);
    });

    // 3. ReadStatus 생성
    UserStatus userStatus = new UserStatus(user);

    //4. DB저장
    UserStatus createdUserStatus = this.userStatusRepository.save(userStatus);

    return userStatusMapper.toDto(createdUserStatus);
  }

  @Override
  public UserStatusDto find(UUID userStatusId) {
    return this.userStatusRepository
        .findById(userStatusId)
        .map(userStatusMapper::toDto)
        .orElseThrow(
            () -> new NoSuchElementException("userStatus with id " + userStatusId + " not found"));
  }

  @Override
  public List<UserStatusDto> findAll() {
    return this.userStatusRepository.findAll().stream()
        .map(userStatusMapper::toDto).toList();
  }

  @Override
  public UserStatusDto update(UUID userStatusId, UserStatusUpdateRequest updateRequest) {
    UserStatus userStatus = this.userStatusRepository
        .findById(userStatusId)
        .orElseThrow(
            () -> new NoSuchElementException("userStatus with id " + userStatusId + " not found"));

    userStatus.update(updateRequest.newLastActiveAt());

    /* 업데이트 후 다시 DB 저장 */
    this.userStatusRepository.save(userStatus);

    return this.userStatusRepository
        .findById(userStatusId)
        .map(userStatusMapper::toDto)
        .orElseThrow(
            () -> new NoSuchElementException("userStatus with id " + userStatusId + " not found"));
  }

  @Override
  public UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest updateRequest) {
    UserStatus userStatus = this.userStatusRepository
        .findByUserId(userId)
        .orElseThrow(
            () -> new NoSuchElementException("userStatus with userId " + userId + " not found"));

    userStatus.update(updateRequest.newLastActiveAt());

    /* 업데이트 후 다시 DB 저장 */
    this.userStatusRepository.save(userStatus);

    return this.userStatusRepository
        .findByUserId(userId)
        .map(userStatusMapper::toDto)
        .orElseThrow(
            () -> new NoSuchElementException("userStatus with userId " + userId + " not found"));
  }

  @Override
  public void delete(UUID userStatusId) {
    if (!this.userStatusRepository.existsById(userStatusId)) {
      throw new NoSuchElementException("userStatus with id " + userStatusId + " not found");
    }
    this.userStatusRepository.deleteById(userStatusId);
  }
}
