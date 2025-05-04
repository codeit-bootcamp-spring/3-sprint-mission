package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.dto.UserStatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.service.dto.UserStatus.UserStatusUpdateRequest;
import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    public UserStatus create(UserStatusCreateRequest request);

    public UserStatus find(UUID id);

    public List<UserStatus> findAll();

    public UserStatus update(UserStatusUpdateRequest request);

    public UserStatus updateByUserId(UserStatusUpdateRequest request);

    public void delete(UUID id);
}
