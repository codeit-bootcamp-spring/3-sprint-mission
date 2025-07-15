package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.readStatus.*;
import com.sprint.mission.discodeit.dto.readStatus.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readStatus.request.ReadStatusUpdateRequest;

import java.util.List;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.service
 * fileName       : ReadStatusService
 * author         : doungukkim
 * date           : 2025. 4. 28.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 28.        doungukkim       최초 생성
 */

public interface ReadStatusService {

    List<ReadStatusResponse> findAllByUserId(UUID userId);

    ReadStatusResponse create(ReadStatusCreateRequest request);

    ReadStatusResponse update(UUID readStatusId, ReadStatusUpdateRequest request);

}
