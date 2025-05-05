package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.entity.UserStatus;
import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserStatus create(UserStatusCreateRequest userStatusCreateRequest);
    UserStatus find(UUID id);
    List<UserStatus> findAll();
    UserStatus updateByStatusId(UserStatusUpdateRequest userStatusUpdateRequest);
    UserStatus updateByUserId(UUID userId);
    void delete(UUID id);
}
