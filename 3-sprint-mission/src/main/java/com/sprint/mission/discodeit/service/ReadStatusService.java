package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.ReadStatusDTO;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import java.util.List;
import java.util.UUID;

public interface ReadStatusService {

  ReadStatusDTO create(ReadStatusCreateRequest request);

  ReadStatusDTO find(UUID id);

  List<ReadStatusDTO> findAll();

  //    List<ReadStatus> findAllByChannelId(UUID channelId);
  List<ReadStatusDTO> findAllByUserId(UUID userId);

  ReadStatusDTO update(UUID id, ReadStatusUpdateRequest request);

  void delete(UUID id);
}
