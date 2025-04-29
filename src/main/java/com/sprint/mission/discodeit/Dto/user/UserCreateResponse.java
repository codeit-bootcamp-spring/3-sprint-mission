package com.sprint.mission.discodeit.Dto.user;

import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.Dto.user
 * fileName       : UserCreateResponse
 * author         : doungukkim
 * date           : 2025. 4. 29.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 29.        doungukkim       최초 생성
 */
public record UserCreateResponse(
        UUID id,
        String username,
        String password,
        String email,
        UUID profileId,
        UUID userStatusId
) {
}
