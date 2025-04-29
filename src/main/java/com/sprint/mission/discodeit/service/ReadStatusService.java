package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.ReadStatusUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    public ReadStatusResponse create(ReadStatusCreateRequest createRequest);

    public ReadStatusResponse find(UUID readStatusId);

    public List<ReadStatusResponse> findAllByUserId(UUID userId);

    public ReadStatusResponse update(ReadStatusUpdateRequest readStatusUpdateRequest);

    public void delete(UUID readStatusId);
}
