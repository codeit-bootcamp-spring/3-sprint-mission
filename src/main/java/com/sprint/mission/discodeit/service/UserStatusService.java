package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserStatus create(UserStatusCreateRequest userStatusCreateRequest);
    UserStatus find(UUID id);
    List<UserStatus> findAll();
    UserStatus updateByUserId(UUID userId, UserStatusUpdateRequest userStatusUpdateRequest);
    void delete(UUID id);
}
