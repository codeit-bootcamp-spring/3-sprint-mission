package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import java.util.List;
import java.util.UUID;

public interface UserStatusService {

  public UserStatus create(UserStatusCreateRequest request);

  public UserStatus find(UUID id);

  public List<UserStatus> findAll();

  public UserStatus update(UUID userStatusId, UserStatusUpdateRequest request);

  public UserStatus updateByUserId(UUID userId, UserStatusUpdateRequest request);

  public void delete(UUID id);
}
