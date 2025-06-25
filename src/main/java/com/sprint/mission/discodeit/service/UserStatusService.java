package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public interface UserStatusService {

  UserStatusDto create(@Valid UserStatusCreateRequest request);

  UserStatusDto find(@NotNull UUID userStatusId);

  List<UserStatusDto> findAll();

  UserStatusDto update(@NotNull UUID userStatusId, @Valid UserStatusUpdateRequest request);

  UserStatusDto updateByUserId(@NotNull UUID userId, @Valid UserStatusUpdateRequest request);

  void delete(@NotNull UUID userStatusId);
}
