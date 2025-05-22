package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.UserStatusAlreadyExistsException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

  private final UserStatusRepository userStatusRepository;
  private final UserRepository userRepository;

  @Override
  public UserStatus create(UserStatusCreateRequest createRequest) {
    //1.  `User`가 존재하지 않으면 예외 발생
    if (this.userRepository.existsById(createRequest.userId())) {
      throw new NoSuchElementException("User with id " + createRequest.userId() + " not found");
    }

    //2.  같은 `User`와 관련된 객체가 이미 존재하면 예외를 발생
    this.userStatusRepository.findById(createRequest.userId()).ifPresent((userStatus) -> {
      throw new UserStatusAlreadyExistsException(userStatus);
    });

    // 3. ReadStatus 생성
    UserStatus userStatus = new UserStatus(createRequest.userId());

    //4. DB저장
    return this.userStatusRepository.save(userStatus);
  }

  @Override
  public UserStatus find(UUID userStatusId) {
    return this.userStatusRepository
        .findById(userStatusId)
        .orElseThrow(
            () -> new NoSuchElementException("userStatus with id " + userStatusId + " not found"));
  }

  @Override
  public List<UserStatus> findAll() {
    return this.userStatusRepository.findAll().stream().toList();
  }

  @Override
  public UserStatus update(UUID userStatusId, UserStatusUpdateRequest updateRequest) {
    UserStatus userStatus = this.userStatusRepository
        .findById(userStatusId)
        .orElseThrow(
            () -> new NoSuchElementException("userStatus with id " + userStatusId + " not found"));

    userStatus.update(updateRequest.newLastActiveAt());

    /* 업데이트 후 다시 DB 저장 */
    this.userStatusRepository.save(userStatus);

    return this.userStatusRepository
        .findById(userStatusId)
        .orElseThrow(
            () -> new NoSuchElementException("userStatus with id " + userStatusId + " not found"));
  }

  @Override
  public UserStatus updateByUserId(UUID userId, UserStatusUpdateRequest updateRequest) {
    UserStatus userStatus = this.userStatusRepository
        .findByUserId(userId)
        .orElseThrow(
            () -> new NoSuchElementException("userStatus with userId " + userId + " not found"));

    userStatus.update(updateRequest.newLastActiveAt());

    /* 업데이트 후 다시 DB 저장 */
    this.userStatusRepository.save(userStatus);

    return this.userStatusRepository
        .findByUserId(userId)
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
