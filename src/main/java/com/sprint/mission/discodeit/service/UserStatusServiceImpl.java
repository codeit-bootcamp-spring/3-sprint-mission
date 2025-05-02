package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.common.exception.UserException;
import com.sprint.mission.discodeit.common.exception.UserStatusException;
import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserStatusServiceImpl implements UserStatusService {

  private final UserStatusRepository userStatusRepository;
  private final UserRepository userRepository;

  @Override
  public UserStatus create(UserStatusCreateRequest request) {
    UUID userId = request.userId();

    User user = userRepository.findById(userId)
        .orElseThrow(() -> UserException.notFound(userId));

    if (userStatusRepository.findByUserId(userId).isPresent()) {
      throw UserStatusException.duplicate(userId);
    }

    UserStatus userStatus = UserStatus.create(userId);
    return userStatusRepository.save(userStatus);
  }

  @Override
  public UserStatus find(UUID id) {
    return userStatusRepository.findById(id)
        .orElseThrow(() -> UserStatusException.notFound(id));
  }

  @Override
  public Optional<UserStatus> findByUserId(UUID userId) {
    return userStatusRepository.findByUserId(userId);
  }

  @Override
  public List<UserStatus> findAll() {
    return userStatusRepository.findAll();
  }

  @Override
  public UserStatus update(UserStatusUpdateRequest request) {
    UUID id = request.userId();
    UserStatus userStatus = userStatusRepository.findById(id)
        .orElseThrow(() -> UserStatusException.notFound(id));

    userStatus.updateLastActiveAt();
    return userStatus;
  }

  @Override
  public UserStatus updateByUserId(UUID userId) {
    UserStatus userStatus = userStatusRepository.findByUserId(userId)
        .orElseThrow(() -> UserStatusException.notFoundByUserId(userId));

    userStatus.updateLastActiveAt();
    return userStatus;
  }

  @Override
  public void delete(UUID id) {
    UserStatus userStatus = userStatusRepository.findById(id)
        .orElseThrow(() -> UserStatusException.notFound(id));

    userStatusRepository.delete(id);
  }
}
