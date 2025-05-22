package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.common.ErrorMessages;
import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BasicUserStatusService implements UserStatusService {

  public final UserRepository userRepository;
  public final UserStatusRepository userStatusRepository;


  @Override
  public UserStatus create(UserStatusCreateRequest request) {
    UUID userId = request.userId();

    if (!userRepository.existsById(userId)) {
      throw new NoSuchElementException(
          ErrorMessages.format("User", ErrorMessages.ERROR_NOT_FOUND));
    }

    if (userStatusRepository.findByUserId(userId).isPresent()) {
      throw new IllegalArgumentException(
          ErrorMessages.format("UserStatus", ErrorMessages.ERROR_EXISTS));
    }

    Instant lastOnlineTime = request.lastActiveAt();
    UserStatus status = new UserStatus(request.userId(), lastOnlineTime);
    return userStatusRepository.save(status);
  }

  @Override
  public UserStatus find(UUID id) {
    return userStatusRepository.find(id)
        .orElseThrow(() -> new NoSuchElementException(
            ErrorMessages.format("UserStatus", ErrorMessages.ERROR_NOT_FOUND)));
  }

  @Override
  public List<UserStatus> findAll() {
    return userStatusRepository.findAll().stream()
        .toList();
  }

  @Override
  public UserStatus update(UUID userStatusId, UserStatusUpdateRequest request) {
    Instant newLastActiveAt = request.newLastActiveAt();

    UserStatus userStatus = userStatusRepository.find(userStatusId)
        .orElseThrow(() -> new NoSuchElementException(
            ErrorMessages.format("UseStatus", ErrorMessages.ERROR_NOT_FOUND)));

    userStatus.update(newLastActiveAt);
    return userStatusRepository.save(userStatus);
  }

  @Override
  public UserStatus updateByUserId(UUID userId, UserStatusUpdateRequest request) {
    Instant newLastActiveAt = request.newLastActiveAt();

    UserStatus userStatus = userStatusRepository.findByUserId(userId)
        .orElseThrow(
            () -> new NoSuchElementException(
                ErrorMessages.format("UserStatus", ErrorMessages.ERROR_NOT_FOUND)));
    userStatus.update(newLastActiveAt);

    return userStatusRepository.save(userStatus);
  }

  @Override
  public void delete(UUID id) {
    if (!userStatusRepository.existsById(id)) {
      throw new NoSuchElementException(
          ErrorMessages.format("UserStatus", ErrorMessages.ERROR_NOT_FOUND));
    }
    userStatusRepository.deleteById(id);
  }
}
