package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.Dto.userStatus.JpaUserStatusResponse;
import com.sprint.mission.discodeit.Dto.userStatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.Dto.userStatus.UserStatusCreateResponse;
import com.sprint.mission.discodeit.Dto.userStatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.service.jcf fileName       : UserStatusService
 * author         : doungukkim date           : 2025. 4. 28. description    :
 * =========================================================== DATE              AUTHOR
 * NOTE ----------------------------------------------------------- 2025. 4. 28.        doungukkim
 * 최초 생성
 */

public interface UserStatusService {

  JpaUserStatusResponse updateByUserId(UUID userId, Instant newLastActiveAt);  // throw

//  UserStatusCreateResponse create(UserStatusCreateRequest request);
//  UserStatus find(UUID userStatusId);
//
//  List<UserStatus> findAll();
//
//  void update(UserStatusUpdateRequest request);
//
//
//  void delete(UUID userStatusId);
}
