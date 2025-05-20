package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    ReadStatus create(ReadStatusCreateRequest readStatusCreateRequest);
    ReadStatus find(UUID id);
    List<ReadStatus> findAllByUserId(UUID id);
    List<ReadStatus> findAllByChannelId(UUID channelId);
    ReadStatus update(ReadStatusUpdateRequest readStatusUpdateRequest);
    void delete(UUID id);
}
