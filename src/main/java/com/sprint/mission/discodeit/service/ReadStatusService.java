package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.CreateReadStatusRequest;
import com.sprint.mission.discodeit.dto.UpdateReadStatusRequest;
import com.sprint.mission.discodeit.entitiy.ReadStatus;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    public ReadStatus create(CreateReadStatusRequest request);
    public ReadStatus find(UUID id);
    public List<ReadStatus> findAllByUserId(UUID userId);
    public void update(UpdateReadStatusRequest request);
    public void delete(UUID readStatusId);
}
