package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.UserStatusResponse;
import com.sprint.mission.discodeit.dto.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatusType;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    public UserStatusResponse create(UserStatusCreateRequest createRequest);

    public UserStatusResponse find(UUID userStatusId);

    public List<UserStatusResponse> findAll();

    public UserStatusResponse update(UserStatusUpdateRequest updateRequest);

    public UserStatusResponse updateByUserId(UUID userId, UserStatusType status);

    public void delete(UUID userStatusId);
}
