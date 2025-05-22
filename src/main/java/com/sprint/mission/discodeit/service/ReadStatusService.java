package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import java.util.List;
import java.util.UUID;

public interface ReadStatusService {

  public ReadStatus create(ReadStatusCreateRequest request);

  public ReadStatus find(UUID id);

  public List<ReadStatus> findAllByUserId(UUID userId);

  public ReadStatus update(UUID readStatusId, ReadStatusUpdateRequest request);

  public void delete(UUID id);
}
