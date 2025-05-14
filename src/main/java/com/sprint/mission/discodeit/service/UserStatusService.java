package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.CreateUserStatusRequest;
import com.sprint.mission.discodeit.dto.UpdateUserStatusRequest;
import com.sprint.mission.discodeit.entitiy.UserStatus;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {

    public UserStatus create(CreateUserStatusRequest request);
    public UserStatus find(UUID userStatusId);
    public UserStatus findByUserId(UUID userId);
    public List<UserStatus> findAll();
    public void update(UpdateUserStatusRequest request);
    public void updateByUserId(UpdateUserStatusRequest request);
    public void delete(UUID userStatusId);
}
