package com.sprint.mission.discodeit.Dto.message;

import com.sprint.mission.discodeit.Dto.binaryContent.JpaBinaryContentResponse;
import com.sprint.mission.discodeit.Dto.user.JpaUserResponse;
import lombok.Builder;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * PackageName  : com.sprint.mission.discodeit.Dto.message
 * FileName     : MessageCreateResponse
 * Author       : dounguk
 * Date         : 2025. 5. 30.
 */
@Builder
public record JpaMessageResponse(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        String content,
        UUID channelId,
        JpaUserResponse author,
        List<JpaBinaryContentResponse> attachments
) {
}
