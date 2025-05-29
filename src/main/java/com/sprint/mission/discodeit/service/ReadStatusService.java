package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ReadStatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.ReadStatusDto;
import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    public ReadStatusDto create(ReadStatusCreateRequest request);

    public ReadStatusDto find(UUID readStatusId);

    public List<ReadStatusDto> findAllByUserId(UUID userId);

    public ReadStatusDto update(UUID readStatusId, ReadStatusUpdateRequest request);

    public void delete(UUID readStatusId);
}
