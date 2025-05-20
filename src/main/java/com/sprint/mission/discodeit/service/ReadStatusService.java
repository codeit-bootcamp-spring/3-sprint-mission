package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entitiy.ReadStatus;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {

  public ReadStatus create(ReadStatusCreateRequest request);

  public ReadStatus find(UUID id);

  public List<ReadStatus> findAllByUserId(UUID userId);

  public void update(UUID readStatusId, ReadStatusUpdateRequest request);

  public void delete(UUID readStatusId);
}
