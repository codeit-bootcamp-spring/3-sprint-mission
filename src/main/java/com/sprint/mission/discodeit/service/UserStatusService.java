package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.UUID;

public interface UserStatusService {
  UserStatus create(UserStatusCreateRequest request);
  UserStatus findById(UUID id);
  UserStatus update(UUID userStatusId, UserStatusUpdateRequest request);
  UserStatus updateByUserId(UUID userId, UserStatusUpdateRequest request);
  void delete(UUID userId);
}

