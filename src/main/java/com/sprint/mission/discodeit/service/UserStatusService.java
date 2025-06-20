package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import java.util.List;
import java.util.UUID;

public interface UserStatusService {

  public UserStatusDto create(UserStatusCreateRequest createRequest);

  public UserStatusDto find(UUID userStatusId);

  public List<UserStatusDto> findAll();

  public UserStatusDto update(UUID userStatusId, UserStatusUpdateRequest updateRequest);

  public UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest updateRequest);

  public void delete(UUID userStatusId);
}
