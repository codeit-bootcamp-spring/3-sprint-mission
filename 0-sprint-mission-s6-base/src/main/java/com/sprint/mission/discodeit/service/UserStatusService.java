package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.UserStatusRequest;
import com.sprint.mission.discodeit.dto.response.UserStatusResponse;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {

  UserStatusResponse create(UUID userId);

  UserStatusResponse find(UUID id);

  UserStatusResponse findByUserId(UUID userId);

  List<UserStatusResponse> findAll();

  UserStatusResponse updateByUserId(UUID userId, UserStatusRequest.Update request);

  void delete(UUID id);

  void deleteByUserId(UUID userId);
}
