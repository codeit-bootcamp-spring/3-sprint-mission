package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserStatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.UserStatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.UserStatusDto;
import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    public UserStatusDto create(UserStatusCreateRequest request);

    public UserStatusDto find(UUID id);

    public List<UserStatusDto> findAll();

    public UserStatusDto update(UUID id, UserStatusUpdateRequest request);

    public UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest request);

    public void delete(UUID id);
}
