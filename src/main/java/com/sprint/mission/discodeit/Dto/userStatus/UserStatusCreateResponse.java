package com.sprint.mission.discodeit.Dto.userStatus;

import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.Dto.userStatus
 * fileName       : UserStatusCreateResponse
 * author         : doungukkim
 * date           : 2025. 4. 29.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 29.        doungukkim       최초 생성
 */
public record UserStatusCreateResponse(UUID id, UUID userId, boolean isOnline) {
}
