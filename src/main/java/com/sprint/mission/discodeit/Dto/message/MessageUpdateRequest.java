package com.sprint.mission.discodeit.Dto.message;

import lombok.Getter;

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
@Getter
public class MessageUpdateRequest {
    UUID messageId;
    String content;

    public MessageUpdateRequest(UUID messageId, String content) {
        this.messageId = messageId;
        this.content = content;
    }

}
