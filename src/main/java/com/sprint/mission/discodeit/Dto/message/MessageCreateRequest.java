package com.sprint.mission.discodeit.Dto.message;

import lombok.Getter;

import java.util.List;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.Dto.message
 * fileName       : MessageCreateDto
 * author         : doungukkim
 * date           : 2025. 4. 28.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 28.        doungukkim       최초 생성
 */
@Getter
public class MessageCreateRequest {
    private UUID senderId;
    private UUID channelId;
    private String content;


    // 메세지
    public MessageCreateRequest(UUID senderId, UUID channelId, String content) {
        this.senderId = senderId;
        this.channelId = channelId;
        this.content = content;
    }
}
