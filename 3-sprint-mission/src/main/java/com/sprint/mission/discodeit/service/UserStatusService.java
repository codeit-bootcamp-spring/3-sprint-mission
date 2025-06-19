package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.UserStatusDTO;
import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import java.util.List;
import java.util.UUID;

public interface UserStatusService {

  UserStatusDTO create(UserStatusCreateRequest request);

  UserStatusDTO find(UUID id);

  List<UserStatusDTO> findAll();

  UserStatusDTO update(UUID id, UserStatusUpdateRequest request);

  UserStatusDTO updateByUserId(UUID userId, UserStatusUpdateRequest request);

  void delete(UUID id);
}
