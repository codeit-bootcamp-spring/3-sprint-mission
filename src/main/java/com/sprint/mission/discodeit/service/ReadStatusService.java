package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ReadStatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatus.ReadStatusDto;
import com.sprint.mission.discodeit.dto.ReadStatus.ReadStatusUpdateRequest;
import java.util.List;
import java.util.UUID;

public interface ReadStatusService {

    ReadStatusDto create(ReadStatusCreateRequest createRequest);

    List<ReadStatusDto> findAllByUserId(UUID userId);

    ReadStatusDto find(UUID id);

    ReadStatusDto update(UUID id, ReadStatusUpdateRequest updateRequest);

    void delete(UUID id);
}
