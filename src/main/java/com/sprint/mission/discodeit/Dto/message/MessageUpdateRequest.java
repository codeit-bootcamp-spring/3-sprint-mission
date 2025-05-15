package com.sprint.mission.discodeit.Dto.message;

import lombok.Getter;

import java.util.Objects;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.Dto.message
 * fileName       : MessageUpdateRequest
 * author         : doungukkim
 * date           : 2025. 4. 28.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 28.        doungukkim       최초 생성
 */

public record MessageUpdateRequest(
        UUID messageId,
        String content) {

    public MessageUpdateRequest {
        Objects.requireNonNull(messageId, "no messageId in request");
        Objects.requireNonNull(content, "no Content in request");
    }
}
