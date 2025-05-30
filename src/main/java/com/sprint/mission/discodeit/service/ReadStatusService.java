package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.Dto.readStatus.*;

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

    List<JpaReadStatusResponse> findAllByUserId(UUID userId);

    JpaReadStatusResponse create(ReadStatusCreateRequest request);

    JpaReadStatusResponse update(UUID readStatusId, ReadStatusUpdateRequest request);

}
