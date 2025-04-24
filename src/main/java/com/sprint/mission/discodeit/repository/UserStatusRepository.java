package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.time.Instant;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.repository
 * fileName       : UserStatusRepository
 * author         : doungukkim
 * date           : 2025. 4. 23.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 23.        doungukkim       최초 생성
 */
public interface UserStatusRepository {

    Instant onlineTime(UUID userStatusId);

    boolean isOnline(UUID userStatusId);

    UserStatus createUserStatus(UUID userId);
}
