package com.sprint.mission.discodeit.Dto.message;

import java.util.List;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.Dto.message
 * fileName       : MessageAttachmentsCreateResponse
 * author         : doungukkim
 * date           : 2025. 4. 29.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 29.        doungukkim       최초 생성
 */
public record MessageAttachmentsCreateResponse(UUID id, UUID senderId, UUID channelId, List<UUID> attachmentIds) {
}
