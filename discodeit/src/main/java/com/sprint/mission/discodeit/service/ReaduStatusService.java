package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.readstatus.CreateReadStatusRequest;
import com.sprint.mission.discodeit.dto.readstatus.UpdateReadStatusRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.UUID;

public interface ReaduStatusService {
    ReadStatus create(CreateReadStatusRequest createReadStatusRequest);
    ReadStatus find(UUID readStatusId);
    List<ReadStatus> findAll();
    List<ReadStatus> findAllByUserId(UUID userId);
    ReadStatus update(UUID userId, UpdateReadStatusRequest updateReadStatusRequest);
    void delete(UUID userId);
}
