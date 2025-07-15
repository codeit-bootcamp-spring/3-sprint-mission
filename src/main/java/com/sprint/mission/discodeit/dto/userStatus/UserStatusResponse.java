package com.sprint.mission.discodeit.dto.userStatus;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.Dto.userStatus fileName       :
 * UpdateUserStatusResponse author         : doungukkim date           : 2025. 5. 15. description :
 * =========================================================== DATE              AUTHOR NOTE
 * ----------------------------------------------------------- 2025. 5. 15.        doungukkim 최초 생성
 */
@Builder
public record UserStatusResponse(
    UUID id,
    UUID userId,
    Instant lastActiveAt
) {

}
