package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.response.UserStatusResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.UserException;
import com.sprint.mission.discodeit.exception.UserStatusException;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicUserStatusService implements UserStatusService {

  //  private static final Logger log = LogManager.getLogger(BasicUserStatusService.class);
  private final UserStatusRepository userStatusRepository;
  private final UserRepository userRepository;
  private final UserStatusMapper userStatusMapper;

  @Override
  public UserStatusResponse create(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> UserException.notFound(userId));

    if (userStatusRepository.findByUserId(userId).isPresent()) {
      throw UserStatusException.duplicate(userId);
    }

    UserStatus saved = userStatusRepository.save(UserStatus.create(user));
    return userStatusMapper.toResponse(saved);
  }

  @Override
  public UserStatusResponse find(UUID userStatusId) {
    return userStatusRepository.findById(userStatusId)
        .map(userStatusMapper::toResponse)
        .orElseThrow(() -> UserStatusException.notFound(userStatusId));
  }

  @Override
  public UserStatusResponse findByUserId(UUID userId) {
    return userStatusRepository.findByUserId(userId)
        .map(userStatusMapper::toResponse)
        .orElseThrow(() -> UserStatusException.notFound(userId));
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
        .orElseThrow(() -> UserStatusException.notFound(userStatusId));

    userStatus.updateLastActiveAt();
    return userStatusMapper.toResponse(userStatusRepository.save(userStatus));
  }

  @Override
  public UserStatusResponse updateByUserId(UUID userId) {
    UserStatus userStatus = userStatusRepository.findByUserId(userId)
        .orElseThrow(() -> UserStatusException.notFoundByUserId(userId));

    userStatus.updateLastActiveAt();
    return userStatusMapper.toResponse(userStatus);
  }

  @Override
  public void delete(UUID userStatusId) {
    userStatusRepository.findById(userStatusId)
        .orElseThrow(() -> UserStatusException.notFound(userStatusId));

    userStatusRepository.deleteById(userStatusId);
  }
}
