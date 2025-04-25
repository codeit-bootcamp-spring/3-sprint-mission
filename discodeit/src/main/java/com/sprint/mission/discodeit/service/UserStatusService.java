package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.userstatus.CreateUserStatusRequest;
import com.sprint.mission.discodeit.dto.userstatus.UpdateUserStatusRequest;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserStatus create(CreateUserStatusRequest createUserStatusRequest);
    UserStatus find(UUID userStatusId);
    List<UserStatus> findAll();
    UserStatus update(UUID userStatusId,UpdateUserStatusRequest updateUserStatusRequest);
    UserStatus updateByUserId(UUID userId,UpdateUserStatusRequest updateUserStatusRequest);
    void delete(UUID userStatusId);
}
