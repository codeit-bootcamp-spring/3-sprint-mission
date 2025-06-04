package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserStatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.UserStatus.UserStatusDto;
import com.sprint.mission.discodeit.dto.UserStatus.UserStatusUpdateRequest;
import java.util.List;
import java.util.UUID;

public interface UserStatusService {

    UserStatusDto create(UserStatusCreateRequest createRequest);

    List<UserStatusDto> findAll();
    
    UserStatusDto find(UUID id);

    UserStatusDto update(UUID userStatusId, UserStatusUpdateRequest updateRequest);

    UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest updateRequest);

    void delete(UUID id);
}
