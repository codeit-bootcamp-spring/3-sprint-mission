package com.sprint.mission.discodeit.dto.userStatus;

import java.util.Objects;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.Dto.userStatus
 * fileName       : UserStatusCreatRequest
 * author         : doungukkim
 * date           : 2025. 4. 28.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 28.        doungukkim       최초 생성
 */

public record UserStatusCreateRequest
        (UUID userId) {
    public UserStatusCreateRequest {
        Objects.requireNonNull(userId, "no userId in request");
    }
}
