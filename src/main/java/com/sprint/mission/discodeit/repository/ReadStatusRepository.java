package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.dto.readStatus.ReadStatusCreateRequestDto;
import com.sprint.mission.discodeit.entity.dto.readStatus.ReadStatusUpdateRequestDto;

import java.util.List;
import java.util.UUID;

public interface ReadStatusRepository {

    public ReadStatus createReadStatus(ReadStatusCreateRequestDto readStatusCreateRequestDto);

    public ReadStatus findReadStatusById(UUID readStatusId);

    public List<ReadStatus> findAllByUserId(UUID userId);

    public boolean updateReadStatus(ReadStatusUpdateRequestDto readStatusUpdateRequestDto);

    public boolean deleteReadStatus(UUID readStatusId);
}
