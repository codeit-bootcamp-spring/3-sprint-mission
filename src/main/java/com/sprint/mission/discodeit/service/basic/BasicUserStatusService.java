package com.sprint.mission.discodeit.service.basic;

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

  private final UserStatusRepository userStatusRepository;
  private final UserRepository userRepository;

  @Override
  public UserStatus find(UUID id) {
    return userStatusRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("UserStatus with id " + id + " not found"));
  }

  @Override
  public List<UserStatus> findAll() {
    return userStatusRepository.findAll();
  }

  @Override
  public UserStatus create(UserStatusCreateRequest request) {
    UUID userId = request.userId();

    if (!userRepository.existsById(userId)) {
      throw new NoSuchElementException("User with id " + userId + " does not exist");
    }

    if (userStatusRepository.findByUserId(userId).isPresent()) {
      throw new IllegalArgumentException("UserStatus for userId " + userId + " already exists");
    }

    UserStatus userStatus = new UserStatus(userId, request.lastActiveAt());
    return userStatusRepository.save(userStatus);
  }

  @Override
  public UserStatus update(UUID userStatusId, UserStatusUpdateRequest request) {
    UserStatus userStatus = userStatusRepository.findById(userStatusId)
        .orElseThrow(() -> new NoSuchElementException(
            "UserStatus with id " + userStatusId + " not found"));

    userStatus.update(request.newLastActiveAt());
    return userStatusRepository.save(userStatus);
  }

  @Override
  public UserStatus updateByUserId(UUID userId, UserStatusUpdateRequest request) {
    Instant newLastAccessedAt = request.newLastActiveAt();

    UserStatus userStatus = userStatusRepository.findByUserId(userId)
        .orElseThrow(
            () -> new NoSuchElementException("UserStatus with userId " + userId + " not found"));
    userStatus.update(newLastAccessedAt);

    return userStatusRepository.save(userStatus);
  }

  @Override
  public UserStatus delete(UUID id) {
    UserStatus userStatus = userStatusRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("UserStatus with id " + id + " not found"));

    userStatusRepository.deleteById(id);
    return userStatus;
  }
}
