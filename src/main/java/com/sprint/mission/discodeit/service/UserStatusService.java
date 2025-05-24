package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entitiy.UserStatus;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {

  public UserStatus create(UserStatusCreateRequest request);

  public UserStatus find(UUID userStatusId);

  public UserStatus findByUserId(UUID userId);

  public List<UserStatus> findAll();

  public void update(UUID userStatusId, UserStatusUpdateRequest request);

  public void updateByUserId(UUID userId, UserStatusUpdateRequest request);

  public void delete(UUID userStatusId);
}
