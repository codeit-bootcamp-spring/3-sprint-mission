package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
  UserStatus create(UserStatusCreateRequest request);
  UserStatus findById(UUID id);
  UserStatus update(UserStatusUpdateRequest request);
  UserStatus updateByUserId(UUID userId);
  void delete(UUID userId);
}

