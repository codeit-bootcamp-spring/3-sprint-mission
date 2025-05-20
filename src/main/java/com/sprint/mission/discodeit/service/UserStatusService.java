package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.dto.userStatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.userStatus.UserStatusUpdateRequest;


public interface UserStatusService {
    UserStatus create(UserStatusCreateRequest request);
    UserStatus find(UUID userStatusId);
    List<UserStatus> findAll();
    UserStatus update(UUID userStatusId, UserStatusUpdateRequest request);
    UserStatus updateByUserId(UUID userId, UserStatusUpdateRequest request);
    void delete(UUID userStatusId);
}
