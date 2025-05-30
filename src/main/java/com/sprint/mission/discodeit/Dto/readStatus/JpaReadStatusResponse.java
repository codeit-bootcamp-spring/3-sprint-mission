package com.sprint.mission.discodeit.Dto.readStatus;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.Dto.readStatus
 * fileName       : FindReadStatusesResponse
 * author         : doungukkim
 * date           : 2025. 5. 16.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 5. 16.        doungukkim       최초 생성
 */
@Builder
public record JpaReadStatusResponse(
        UUID id,
        UUID userId,
        UUID channelId,
        Instant lastReadAt
) {
}
