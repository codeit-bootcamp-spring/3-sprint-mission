package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusRequestDTO;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponseDTO;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserStatus create(UserStatusRequestDTO userStatusRequestDTO);
    UserStatusResponseDTO findById(UUID id);
    List<UserStatusResponseDTO> findAll();
    UserStatusResponseDTO update(UUID id, UserStatusRequestDTO userStatusRequestDTO);
    UserStatusResponseDTO updateByUserId(UUID userId);
    void deleteById(UUID id);
}
