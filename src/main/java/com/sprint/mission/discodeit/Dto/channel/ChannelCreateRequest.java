package com.sprint.mission.discodeit.Dto.channel;

import java.util.Objects;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.Dto.channel
 * fileName       : ChannelCreateRequest
 * author         : doungukkim
 * date           : 2025. 4. 29.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 29.        doungukkim       최초 생성
 */
public record ChannelCreateRequest(UUID userId) {
    public ChannelCreateRequest {
        Objects.requireNonNull(userId, "no userId in request");
    }
}
