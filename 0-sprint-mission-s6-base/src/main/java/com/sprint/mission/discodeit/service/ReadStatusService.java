package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.ReadStatusRequest;
import com.sprint.mission.discodeit.dto.response.ReadStatusResponse;
import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {

  ReadStatusResponse create(ReadStatusRequest.Create request);

  ReadStatusResponse find(UUID id);

  List<ReadStatusResponse> findAllByUserId(UUID userId);
  
  ReadStatusResponse update(UUID id, ReadStatusRequest.Update request);

  void delete(UUID readStatusId);

}
