package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusRequestDTO;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponseDTO;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateDTO;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserStatus create(UserStatusRequestDTO userStatusRequestDTO);
    UserStatusResponseDTO findById(UUID id);
    List<UserStatusResponseDTO> findAll();
    UserStatusResponseDTO update(UUID id, UserStatusUpdateDTO userStatusUpdateDTO);
    UserStatusResponseDTO updateByUserId(UUID userId, UserStatusUpdateDTO userStatusUpdateDTO);
    void deleteById(UUID id);
}
