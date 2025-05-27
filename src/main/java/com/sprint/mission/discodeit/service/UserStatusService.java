package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.user.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserStatus create(UserStatusCreateRequest request);
    UserStatus find(UUID userStatusId);
    List<UserStatus> findAll();
    UserStatus update(UUID userStatusId, UserStatusUpdateRequest request);
    UserStatus updateByUserId(UUID userId, UserStatusUpdateRequest request);
    void delete(UUID userStatusId);
}
