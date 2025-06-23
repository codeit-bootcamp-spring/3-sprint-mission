package com.sprint.mission.discodeit.unit.basic;

import com.sprint.mission.discodeit.dto.userStatus.JpaUserStatusResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.advanced.UserStatusMapper;
import com.sprint.mission.discodeit.repository.jpa.JpaUserRepository;
import com.sprint.mission.discodeit.unit.UserStatusService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.service.basic fileName       :
 * BasicUserStatusService author         : doungukkim date           : 2025. 4. 28. description    :
 * =========================================================== DATE              AUTHOR NOTE
 * ----------------------------------------------------------- 2025. 4. 28.        doungukkim 최초 생성
 */
@Service("basicUserStatusService")
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {
  private final JpaUserRepository userRepository;
  private final UserStatusMapper userStatusMapper;

  private static final Logger log = LoggerFactory.getLogger(BasicUserStatusService.class);

  @Transactional
  @Override
  public JpaUserStatusResponse updateByUserId(UUID userId, Instant newLastActiveAt) {
    Objects.requireNonNull(userId, "no userId in param");

    if (userRepository.count() < 1) log.warn("any user exists");

    User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(Map.of("userId", userId)));
    UserStatus userStatus = user.getStatus();
    userStatus.setLastActiveAt(newLastActiveAt);

    JpaUserStatusResponse response = userStatusMapper.toDto(userStatus);
    return response;
  }

}
