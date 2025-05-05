package com.sprint.mission.discodeit.Dto.channel;

import java.util.Objects;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.Dto.channel
 * fileName       : ChannelFindByUserIdRequest
 * author         : doungukkim
 * date           : 2025. 4. 29.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 29.        doungukkim       최초 생성
 */
public record ChannelFindByUserIdRequest(UUID userId) {
    public ChannelFindByUserIdRequest {
        Objects.requireNonNull(userId, "userId is null in request");
    }
}
