package com.sprint.mission.discodeit.Dto.userStatus;

import lombok.Getter;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.Dto.userStatus
 * fileName       : UserStatusUpdateRequest
 * author         : doungukkim
 * date           : 2025. 4. 28.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 28.        doungukkim       최초 생성
 */

public record UserStatusUpdateRequest(UUID userStatusId, Instant newTime) {
    public UserStatusUpdateRequest {
        Objects.requireNonNull(userStatusId, "no userId in request");
        Objects.requireNonNull(newTime, "no newTime in request");
    }
}
