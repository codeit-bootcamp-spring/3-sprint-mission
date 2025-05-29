package com.sprint.mission.discodeit.Dto.message;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.time.Instant;
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
public record MessageAttachmentsCreateResponse(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        String content,
        UUID channelId,
        UUID authorId,
        List<UUID> attachmentIds) {
}
