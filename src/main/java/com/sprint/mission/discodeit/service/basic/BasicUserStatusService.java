package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class BasicUserStatusService implements UserStatusService {

  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;

  public BasicUserStatusService(UserRepository userRepository, UserStatusRepository userStatusRepository) {
    this.userRepository = userRepository;
    this.userStatusRepository = userStatusRepository;
  }

  @Override
  public UserStatus create(UserStatusCreateRequest request) {
    UUID userId = request.id();

    if (userRepository.findById(userId).isEmpty()) {
      throw new IllegalArgumentException("userStatus가 존재하지 않음");
    }

    if (userStatusRepository.findByUserId(userId).isPresent()) {
      throw new IllegalStateException("userStatus가 존재하지 않음");
    }

    UserStatus status = new UserStatus(userId);
    return userStatusRepository.save(status);
  }

  @Override
  public UserStatus findById(UUID id) {
    return userStatusRepository.find(id)
        .orElseThrow(() -> new NoSuchElementException("userStatus가 존재하지 않음"));
  }

  @Override
  public UserStatus update(UserStatusUpdateRequest request) {
    UserStatus status = findById(request.id());
    status.updateTimestamp();
    return userStatusRepository.save(status);
  }

  @Override
  public UserStatus updateByUserId(UUID userId) {
    UserStatus status = userStatusRepository.findAll().stream()
        .filter(s -> s.getUserId().equals(userId))
        .findFirst()
        .orElseThrow(() -> new NoSuchElementException("userStatus가 존재하지 않음"));

    status.updateTimestamp();
    return userStatusRepository.save(status);
  }

  @Override
  public void delete(UUID id) {
    if (userStatusRepository.find(id).isEmpty()) {
      throw new NoSuchElementException("userStatus가 존재하지 않음");
    }
    userStatusRepository.delete(id);
  }
}

