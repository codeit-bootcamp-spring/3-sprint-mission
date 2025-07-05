package com.sprint.mission.discodeit.service.basic;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sprint.mission.discodeit.dto.response.UserStatusResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.DuplicateUserStatusException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserStatusNotFoundException;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicUserStatusService implements UserStatusService {

  private final UserStatusRepository userStatusRepository;
  private final UserRepository userRepository;
  private final UserStatusMapper userStatusMapper;

  @Override
  public UserStatusResponse create(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId.toString()));

    if (userStatusRepository.findByUserId(userId).isPresent()) {
      throw new DuplicateUserStatusException(userId.toString());
    }

    UserStatus saved = userStatusRepository.save(UserStatus.create(user));
    return userStatusMapper.toResponse(saved);
  }

  @Override
  public UserStatusResponse find(UUID userStatusId) {
    return userStatusRepository.findById(userStatusId)
        .map(userStatusMapper::toResponse)
        .orElseThrow(() -> new UserStatusNotFoundException(userStatusId.toString()));
  }

  @Override
  public UserStatusResponse findByUserId(UUID userId) {
    return userStatusRepository.findByUserId(userId)
        .map(userStatusMapper::toResponse)
        .orElseThrow(() -> new UserStatusNotFoundException(userId.toString(), true));
  }

  @Override
  public List<UserStatusResponse> findAll() {
    return userStatusRepository.findAll().stream()
        .map(userStatusMapper::toResponse)
        .collect(Collectors.toList());
  }

  @Override
  public UserStatusResponse update(UUID userStatusId) {
    UserStatus userStatus = userStatusRepository.findById(userStatusId)
        .orElseThrow(() -> new UserStatusNotFoundException(userStatusId.toString()));

    userStatus.updateLastActiveAt();
    return userStatusMapper.toResponse(userStatusRepository.save(userStatus));
  }

  @Override
  public UserStatusResponse updateByUserId(UUID userId) {
    UserStatus userStatus = userStatusRepository.findByUserId(userId)
        .orElseThrow(() -> new UserStatusNotFoundException(userId.toString(), true));

    userStatus.updateLastActiveAt();
    return userStatusMapper.toResponse(userStatus);
  }

  @Override
  public void delete(UUID userStatusId) {
    userStatusRepository.findById(userStatusId)
        .orElseThrow(() -> new UserStatusNotFoundException(userStatusId.toString()));

    userStatusRepository.deleteById(userStatusId);
  }
}
