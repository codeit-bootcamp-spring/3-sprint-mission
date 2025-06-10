package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import java.util.UUID;

public interface UserStatusService {

    UserStatusDto create(UserStatusCreateRequest request);

    UserStatusDto findById(UUID id);

    UserStatusDto update(UUID userStatusId, UserStatusUpdateRequest request);

    UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest request);

    void delete(UUID userId);
}

