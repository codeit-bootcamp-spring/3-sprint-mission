package com.sprint.mission.discodeit.unit;

import com.sprint.mission.discodeit.dto.userStatus.JpaUserStatusResponse;

import java.time.Instant;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.service.jcf fileName       : UserStatusService
 * author         : doungukkim date           : 2025. 4. 28. description    :
 * =========================================================== DATE              AUTHOR
 * NOTE ----------------------------------------------------------- 2025. 4. 28.        doungukkim
 * 최초 생성
 */

public interface UserStatusService {

  JpaUserStatusResponse updateByUserId(UUID userId, Instant newLastActiveAt);  // throw
}
