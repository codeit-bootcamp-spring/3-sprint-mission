package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.UserStatusDTO;
import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserStatus create(UserStatusCreateRequest request);
    UserStatusDTO find(UUID id);
    List<UserStatusDTO> findAll();
    UserStatus update(UUID id, UserStatusUpdateRequest request);
    UserStatus updateByUserId(UUID userId, UserStatusUpdateRequest request);
    void delete(UUID id);
}
