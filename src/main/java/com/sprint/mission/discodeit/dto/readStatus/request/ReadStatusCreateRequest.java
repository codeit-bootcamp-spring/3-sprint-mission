package com.sprint.mission.discodeit.dto.readStatus.request;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.Dto.userStatus
 * fileName       : ReadStatusCreateRequest
 * author         : doungukkim
 * date           : 2025. 4. 28.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 28.        doungukkim       최초 생성
 */

public record ReadStatusCreateRequest(UUID userId, UUID channelId, Instant lastReadAt) {

    public ReadStatusCreateRequest {
        Objects.requireNonNull(userId, "no userId in request");
        Objects.requireNonNull(channelId, "no channelId in request");
        Objects.requireNonNull(lastReadAt, "no lasReadAt in request");
    }
}

