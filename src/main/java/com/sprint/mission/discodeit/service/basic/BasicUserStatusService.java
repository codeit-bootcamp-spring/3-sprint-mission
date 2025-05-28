package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicUserStatusService implements UserStatusService {

  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;

  @Override
  public UserStatus create(UserStatusCreateRequest request) {
    UUID userId = request.userId();

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

    if (userStatusRepository.findByUserId(userId).isPresent()) {
      throw new IllegalArgumentException("UserStatus already exists for user with id " + userId);
    }

    Instant lastActiveAt = request.lastActiveAt();
    UserStatus userStatus = new UserStatus(user, lastActiveAt);
    return userStatusRepository.save(userStatus);
  }

  @Override
  public UserStatus find(UUID userStatusId) {
    return userStatusRepository.findById(userStatusId)
        .orElseThrow(
            () -> new NoSuchElementException("UserStatus with id " + userStatusId + " not found"));
  }

  @Override
  public List<UserStatus> findAll() {
    return userStatusRepository.findAll();
  }

  @Override
  public UserStatus update(UUID userStatusId, UserStatusUpdateRequest request) {

    UserStatus userStatus = find(userStatusId);

    Instant newLastOnline = request.newLastActiveAt();

    userStatus.update(newLastOnline);

    return userStatus;
  }

  @Override
  public UserStatus updateByUserId(UUID userId, UserStatusUpdateRequest request) {

    Instant newLastOnlineAt = request.newLastActiveAt();

    UserStatus userStatus = userStatusRepository.findByUserId(userId)
        .orElseThrow(
            () -> new NoSuchElementException("UserStatus with userId " + userId + " not found"));
    userStatus.update(newLastOnlineAt);

    return userStatus;
  }

  @Override
  public void delete(UUID userStatusId) {
    if (!userStatusRepository.existsById(userStatusId)) {
      throw new NoSuchElementException("UserStatus with id " + userStatusId + " not found");
    }
    userStatusRepository.deleteById(userStatusId);
  }
}
