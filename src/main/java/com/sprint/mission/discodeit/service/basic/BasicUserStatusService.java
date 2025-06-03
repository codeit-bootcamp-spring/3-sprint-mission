package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userStatus.JpaUserStatusResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.advanced.AdvancedUserStatusMapper;
import com.sprint.mission.discodeit.mapper.original.UserStatusMapper;
import com.sprint.mission.discodeit.repository.jpa.JpaUserRepository;
import com.sprint.mission.discodeit.repository.jpa.JpaUserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

/**
 * packageName    : com.sprint.mission.discodeit.service.basic fileName       :
 * BasicUserStatusService author         : doungukkim date           : 2025. 4. 28. description    :
 * =========================================================== DATE              AUTHOR NOTE
 * ----------------------------------------------------------- 2025. 4. 28.        doungukkim 최초 생성
 */
@Service("basicUserStatusService")
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

  private final JpaUserStatusRepository userStatusRepository;
  private final UserStatusMapper userStatusMapper;
  private final AdvancedUserStatusMapper advancedUserStatusMapper;
//  private final UserStatusRepository userStatusRepository;
  private final JpaUserRepository userRepository;

  @Transactional
  @Override
  public JpaUserStatusResponse updateByUserId(UUID userId, Instant newLastActiveAt) {
    Objects.requireNonNull(userId, "no userId in param");
    Objects.requireNonNull(newLastActiveAt, "no userId in param");

    User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("UserStatus with userId " + userId + " not found"));
    UserStatus userStatus = user.getStatus();
    userStatus.setLastActiveAt(newLastActiveAt);

    // original
//    JpaUserStatusResponse response = userStatusMapper.toDto(userStatus);
    // advanced
    JpaUserStatusResponse response = advancedUserStatusMapper.toDto(userStatus);
    return response;
  }

//  @Override
//  public UserStatusCreateResponse create(UserStatusCreateRequest request) {
//    UUID userId = request.userId();
//
//    userRepository.findUserById(userId); // 없으면 throw error 날림
//
//    if (userStatusRepository.findUserStatusByUserId(userId) != null) { // userStatus 이미 있으면 예외 발생
//      throw new RuntimeException("user status already exist");
//    }
//
//    UserStatus userStatus = userStatusRepository.createUserStatus(request.userId());
//    boolean online = userStatusRepository.isOnline(userStatus.getId()); // throw
//
//    return new UserStatusCreateResponse(userStatus.getId(), userStatus.getUserId(), online);
//    return null;
//  }

//
//  @Override
//  public UserStatus find(UUID userStatusId) {
////    Objects.requireNonNull(userStatusId, "no userStatusId param");
////    return userStatusRepository.findById(userStatusId);
//    return null;
//  }
//
//  @Override
//  public List<UserStatus> findAll() {
////    return userStatusRepository.findAllUserStatus();
//    return null;
//  }
//
//  @Override
//  public void update(UserStatusUpdateRequest request) {
////    userStatusRepository.update(
////        request.userStatusId(), request.newTime()
////    );
//  }
//
//  @Override
//  public void delete(UUID userStatusId) {
////    Objects.requireNonNull(userStatusId, "no userStatusId");
////    userStatusRepository.deleteById(userStatusId); // throw
//
//  }
//
//  private static boolean isOnline(UserStatus userStatus) {
//    Instant now = Instant.now();
//    return Duration.between(userStatus.getLastActiveAt(), now).toMinutes() < 5;
//  }
}
