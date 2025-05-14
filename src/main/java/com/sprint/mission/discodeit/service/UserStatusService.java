package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.entity.UserStatusType;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    public UserStatus create(UserStatusCreateRequest createRequest);

    public UserStatus find(UUID userStatusId);

    public List<UserStatus> findAll();

    public UserStatus update(UserStatusUpdateRequest updateRequest);

    public UserStatus updateByUserId(UUID userId, UserStatusType status);

    public void delete(UUID userStatusId);
}
