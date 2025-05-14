package com.sprint.mission.discodeit.Dto.channel;

import lombok.Getter;

import java.util.Objects;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.Dto.channel
 * fileName       : ChannelUpdateRequest
 * author         : doungukkim
 * date           : 2025. 4. 27.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 27.        doungukkim       최초 생성
 */

public record ChannelUpdateRequest
        (UUID channelId,
         String name
        ) {
        public ChannelUpdateRequest {
                Objects.requireNonNull(channelId, "no channelId in request");
                Objects.requireNonNull(name, "no name in request");
        }
}
