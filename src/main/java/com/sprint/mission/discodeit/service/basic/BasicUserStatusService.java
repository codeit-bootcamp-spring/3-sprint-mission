package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicUserStatusService implements UserStatusService {

  private final UserStatusRepository statusRepository;

  @Override
  public UserStatus create(UserStatusCreateRequest request) {
    UserStatus s = new UserStatus(request.userId(), request.lastActiveAt());
    return statusRepository.save(s);
  }

  @Override
  public UserStatus find(UUID userStatusId) {
    return statusRepository.findById(userStatusId)
        .orElseThrow(() -> new NoSuchElementException("UserStatus not found: " + userStatusId));
  }

  @Override
  public List<UserStatus> findAll() {
    return statusRepository.findAll();
  }

  @Override
  public UserStatus update(UUID userStatusId, UserStatusUpdateRequest request) {
    UserStatus s = find(userStatusId);
    s.setLastActiveAt(request.newLastActiveAt());
    return statusRepository.save(s);
  }

  @Override
  public UserStatus updateByUserId(UUID userId, UserStatusUpdateRequest request) {
    UserStatus s = statusRepository.findByUserId(userId)
        .orElseThrow(() -> new NoSuchElementException("UserStatus for user not found: " + userId));
    s.setLastActiveAt(request.newLastActiveAt());
    return statusRepository.save(s);
  }

  @Override
  public void delete(UUID userStatusId) {
    if (!statusRepository.existsById(userStatusId)) {
      throw new NoSuchElementException("UserStatus not found: " + userStatusId);
    }
    statusRepository.deleteById(userStatusId);
  }
}
