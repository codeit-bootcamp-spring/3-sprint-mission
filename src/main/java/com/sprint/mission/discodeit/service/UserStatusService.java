package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusRequestDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponseDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateDto;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserStatusResponseDto create(UserStatusRequestDto userStatusRequestDto);

    UserStatusResponseDto findById(UUID id);

    List<UserStatusResponseDto> findAll();

    UserStatusResponseDto update(UUID id, UserStatusUpdateDto userStatusUpdateDto);

    UserStatusResponseDto updateByUserId(UUID userId, UserStatusUpdateDto userStatusUpdateDto);

    void deleteById(UUID id);
}
