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
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class BasicUserStatusService implements UserStatusService {

  private final UserStatusRepository userStatusRepository;
  private final UserRepository userRepository;
  private final UserStatusMapper userStatusMapper;

  @Override
  public UserStatusDTO create(UserStatusCreateRequest request) {
    UUID userId = request.userId();

    if (!userRepository.existsById(userId)) {
      throw new NoSuchElementException("User with id " + userId + " does not exist");
    }
    if (userStatusRepository.findByUserId(userId).isPresent()) {
      throw new IllegalArgumentException("UserStatus with id " + userId + " already exists");
    }

    User user = userRepository.findById(userId)
        .orElseThrow(
            () -> new NoSuchElementException("User with id " + userId + " does not exist"));
    Optional.ofNullable(user.getUserStatus())
        .ifPresent(userStatus -> {
          throw new IllegalArgumentException("UserStatus already exists");
        });

    Instant lastActiveAt = request.lastLoginTime();
    UserStatus userStatus =
        UserStatus
            .builder()
            .user(user)
            .lastActiveAt(lastActiveAt)
            .build();
    userStatusRepository.save(userStatus);
    return userStatusMapper.toDTO(userStatus);
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
  public UserStatusDTO update(UUID id, UserStatusUpdateRequest request) {
    Instant newLastActiveAt = request.newLastActiveAt();

    UserStatus userStatus = userStatusRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("UserStatus with id " + id + " not found"));
    userStatus.update(newLastActiveAt);

    return userStatusMapper.toDTO(userStatus);
  }

  @Override
  public UserStatusDTO updateByUserId(UUID userId, UserStatusUpdateRequest userStatusUpdateDTO) {
    Instant newLastActiveAt = userStatusUpdateDTO.newLastActiveAt();

    UserStatus userStatus = userStatusRepository.findByUserId(userId)
        .orElseThrow(
            () -> new NoSuchElementException("UserStatus with userId " + userId + " not found"));

    userStatus.update(newLastActiveAt);

    return userStatusMapper.toDTO(userStatus);
  }

  @Override
  public void delete(UUID id) {
    if (!userStatusRepository.existsById(id)) {
      throw new NoSuchElementException("UserStatus with id " + id + " not found");
    }
    userStatusRepository.deleteById(id);
  }

}
