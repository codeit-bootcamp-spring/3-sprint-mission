package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ReadStatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    public ReadStatus create(ReadStatusCreateRequest request);

    public ReadStatus find(UUID readStatusId);

    public List<ReadStatus> findAllByUserId(UUID userId);

    public ReadStatus update(UUID readStatusId, ReadStatusUpdateRequest request);

    public void delete(UUID readStatusId);
}
