package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.repository fileName       : UserStatusRepository
 * author         : doungukkim date           : 2025. 4. 23. description    :
 * =========================================================== DATE              AUTHOR NOTE
 * ----------------------------------------------------------- 2025. 4. 23.        doungukkim 최초 생성
 */
public interface UserStatusRepository {

  boolean isOnline(UUID userStatusId); // throw

  UserStatus createUserStatus(UUID userId);

  UserStatus findUserStatusByUserId(UUID userId); // both : null

  List<UserStatus> findAllUserStatus(); // emptyList

  void deleteById(UUID userStatusId); // throw

  UserStatus findById(UUID userStatusId);

  void update(UUID userStatusId, Instant newTime); // throw

  void updateByUserId(UUID userId, Instant newLastActiveAt);

}
