package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.userStatus.UserStatusResponse;

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

  UserStatusResponse updateByUserId(UUID userId, Instant newLastActiveAt);  // throw
}
