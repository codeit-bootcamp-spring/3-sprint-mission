package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserStatusDTO;
import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class BasicUserStatusService implements UserStatusService {

  private final UserStatusRepository userStatusRepository;
  private final UserRepository userRepository;
  private final UserStatusMapper userStatusMapper;

  @Override
  public UserStatus create(UserStatusCreateRequest request) {
    User user = request.user();

    if (!userRepository.existsById(user.getId())) {
      throw new NoSuchElementException("User with id " + user.getId() + " does not exist");
    }
    if (userStatusRepository.findByUserId(user.getId()).isPresent()) {
      throw new IllegalArgumentException("UserStatus with id " + user.getId() + " already exists");
    }

    Instant lastActiveAt = request.lastLoginTime();
    UserStatus userStatus =
        UserStatus
            .builder()
            .user(user)
            .lastActiveAt(lastActiveAt)
            .build();
    return userStatusRepository.save(userStatus);
  }

  @Override
  @Transactional(readOnly = true)
  public UserStatusDTO find(UUID id) {
    return userStatusRepository.findById(id)
            .map(userStatusMapper::toDTO)
        .orElseThrow(() -> new NoSuchElementException("UserStatus with id " + id + " not found"));
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserStatusDTO> findAll() {
    return userStatusRepository.findAll().stream()
            .map(userStatusMapper::toDTO)
        .toList();
  }

  @Override
  public UserStatus update(UUID id, UserStatusUpdateRequest request) {
    Instant newLastActiveAt = request.newLastActiveAt();

    UserStatus userStatus = userStatusRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("UserStatus with id " + id + " not found"));
    userStatus.update(newLastActiveAt);

    return userStatus;
  }

  @Override
  public UserStatus updateByUserId(UUID userId, UserStatusUpdateRequest userStatusUpdateDTO) {
    Instant newLastActiveAt = userStatusUpdateDTO.newLastActiveAt();

    UserStatus userStatus = userStatusRepository.findByUserId(userId)
        .orElseThrow(
            () -> new NoSuchElementException("UserStatus with userId " + userId + " not found"));

    userStatus.update(newLastActiveAt);

    return userStatus;
  }

  @Override
  public void delete(UUID id) {
    if (!userStatusRepository.existsById(id)) {
      throw new NoSuchElementException("UserStatus with id " + id + " not found");
    }
    userStatusRepository.deleteById(id);
  }

}
