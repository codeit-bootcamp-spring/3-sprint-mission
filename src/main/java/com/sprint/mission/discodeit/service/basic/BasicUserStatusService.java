package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.UserException;
import com.sprint.mission.discodeit.exception.UserStatusException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

  //  private static final Logger log = LogManager.getLogger(BasicUserStatusService.class);
  private final UserStatusRepository userStatusRepository;
  private final UserRepository userRepository;

  @Override
  public UserStatus create(UUID userId) {
    userRepository.findById(userId)
        .orElseThrow(() -> UserException.notFound(userId));

    if (userStatusRepository.findByUserId(userId).isPresent()) {
      throw UserStatusException.duplicate(userId);
    }

    UserStatus userStatus = UserStatus.create(userId);
    return userStatusRepository.save(userStatus);
  }

  @Override
  public UserStatus find(UUID userStatusId) {
    return userStatusRepository.findById(userStatusId)
        .orElseThrow(() -> UserStatusException.notFound(userStatusId));
  }

  @Override
  public UserStatus findByUserId(UUID userId) {
    return userStatusRepository.findByUserId(userId)
        .orElseThrow(() -> UserStatusException.notFound(userId));
  }

  @Override
  public List<UserStatus> findAll() {
    return userStatusRepository.findAll();
  }

  @Override
  public UserStatus update(UUID userId) {
    UserStatus userStatus = userStatusRepository.findById(userId)
        .orElseThrow(() -> UserStatusException.notFound(userId));

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
  public void delete(UUID userStatusId) {
    UserStatus userStatus = userStatusRepository.findById(userStatusId)
        .orElseThrow(() -> UserStatusException.notFound(userStatusId));

    userStatusRepository.delete(userStatusId);
  }
}
