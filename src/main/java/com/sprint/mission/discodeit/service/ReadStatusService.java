package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import java.util.List;
import java.util.UUID;

public interface ReadStatusService {

  public ReadStatusDto create(ReadStatusCreateRequest createRequest);

  public ReadStatusDto find(UUID readStatusId);

  public List<ReadStatusDto> findAllByUserId(UUID userId);

  public ReadStatusDto update(UUID readStatusId, ReadStatusUpdateRequest readStatusUpdateRequest);

  public void delete(UUID readStatusId);
}
