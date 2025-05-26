package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.Dto.readStatus.*;
import com.sprint.mission.discodeit.entity.ReadStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;
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

    List<FindReadStatusesResponse> findAllByUserId(UUID userId);

    ReadStatusCreateResponse create(ReadStatusCreateRequest request);

    UpdateReadStatusResponse update(UUID readStatusId, ReadStatusUpdateRequest request);

    // --------------------------------------------------------------------------------------------------------------

    ReadStatus findById(UUID readStatusId);

    void delete(UUID readStatusId);
}
