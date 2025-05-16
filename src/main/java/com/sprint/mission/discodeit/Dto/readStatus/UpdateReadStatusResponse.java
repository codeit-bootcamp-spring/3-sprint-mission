package com.sprint.mission.discodeit.Dto.readStatus;

import java.time.Instant;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.Dto.readStatus
 * fileName       : UpdateReadStatusResponse
 * author         : doungukkim
 * date           : 2025. 5. 16.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 5. 16.        doungukkim       최초 생성
 */
public record UpdateReadStatusResponse(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        UUID userId,
        UUID channelId,
        Instant lastReadAt
) {
}
