package com.sprint.mission.discodeit.Dto.message;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.Dto.message
 * fileName       : FoundMessagesResponse
 * author         : doungukkim
 * date           : 2025. 5. 16.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 5. 16.        doungukkim       최초 생성
 */
public record FoundMessagesResponse (
    UUID id,
    Instant createdAt,
    Instant updatedAt,
    String content,
    UUID channelId,
    UUID authorId,
    List<UUID> attachmentIds
){

}
