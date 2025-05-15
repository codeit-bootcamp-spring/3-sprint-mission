package com.sprint.mission.discodeit.Dto.user;

import java.util.Objects;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.Dto.user
 * fileName       : UesrFindRequest
 * author         : doungukkim
 * date           : 2025. 5. 9.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 5. 9.        doungukkim       최초 생성
 */
public record UserFindRequest(UUID userId) {
    public UserFindRequest {
        Objects.requireNonNull(userId, "no userId in request");
    }
}
