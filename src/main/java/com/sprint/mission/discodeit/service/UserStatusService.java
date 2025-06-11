package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {

    public UserStatusDto create(UserStatusCreateRequest request);

    public UserStatusDto find(UUID id);

    public List<UserStatusDto> findAll();

    public UserStatusDto update(UUID userStatusId, UserStatusUpdateRequest request);

    public UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest request);

    public void delete(UUID id);
}
