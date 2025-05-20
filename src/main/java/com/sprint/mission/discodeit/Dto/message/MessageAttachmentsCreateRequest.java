package com.sprint.mission.discodeit.Dto.message;

import lombok.Getter;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.Dto.message
 * fileName       : MessageAttachmentsCreateRequest
 * author         : doungukkim
 * date           : 2025. 4. 28.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 28.        doungukkim       최초 생성
 */

public record MessageAttachmentsCreateRequest
        (UUID senderId,
         UUID channelId
        ) {
    public MessageAttachmentsCreateRequest {
        Objects.requireNonNull(senderId,"no senderId in request");
        Objects.requireNonNull(channelId, "no channelId in request");
    }
}
