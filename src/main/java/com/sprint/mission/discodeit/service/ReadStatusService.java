package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    public ReadStatus create(ReadStatusCreateRequest createRequest);

    public ReadStatus find(UUID readStatusId);

    public List<ReadStatus> findAllByUserId(UUID userId);

    public ReadStatus update(UUID readStatusId, ReadStatusUpdateRequest readStatusUpdateRequest);

    public void delete(UUID readStatusId);
}
