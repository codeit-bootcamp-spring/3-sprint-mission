package com.sprint.mission.discodeit.Dto.message;

import lombok.Getter;

import java.util.List;
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
@Getter
public class MessageAttachmentsCreateRequest {
    private UUID senderId;
    private UUID channelId;
    private List<byte[]> attachments;

    // 첨부파일
    public MessageAttachmentsCreateRequest(UUID senderId, UUID channelId, List<byte[]> attachments) {
        this.senderId = senderId;
        this.channelId = channelId;
        this.attachments = attachments;
    }
}
