package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.UserStatusRequest;
import com.sprint.mission.discodeit.dto.response.UserStatusResponse;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {

  UserStatusResponse create(UUID userId);

  UserStatusResponse find(UUID id);

  List<UserStatusResponse> findAll();

  UserStatus update(UUID id, UserStatusRequest.Update request);

  UserStatus updateByUserId(UUID userId, UserStatusRequest.Update request);

  void delete(UUID userStatusId);
}
