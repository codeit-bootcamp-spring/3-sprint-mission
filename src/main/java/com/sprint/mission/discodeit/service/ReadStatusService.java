package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public interface ReadStatusService {

  ReadStatusDto create(@Valid ReadStatusCreateRequest request);

  ReadStatus find(@NotNull UUID readStatusId);

  List<ReadStatusDto> findAllByUserId(@NotNull UUID userId);

  ReadStatusDto update(@NotNull UUID readStatusId, @Valid ReadStatusUpdateRequest request);

  void delete(@NotNull UUID readStatusId);
}
