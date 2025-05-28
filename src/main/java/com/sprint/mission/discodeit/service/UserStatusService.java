package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusRequestDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponseDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateDto;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserStatus create(UserStatusRequestDto userStatusRequestDTO);
    UserStatusResponseDto findById(UUID id);
    List<UserStatusResponseDto> findAll();
    UserStatusResponseDto update(UUID id, UserStatusUpdateDto userStatusUpdateDTO);
    UserStatusResponseDto updateByUserId(UUID userId, UserStatusUpdateDto userStatusUpdateDTO);
    void deleteById(UUID id);
}
