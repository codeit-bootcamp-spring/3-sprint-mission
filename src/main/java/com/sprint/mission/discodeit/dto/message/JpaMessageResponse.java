package com.sprint.mission.discodeit.dto.message;

import com.sprint.mission.discodeit.dto.binaryContent.JpaBinaryContentResponse;
import com.sprint.mission.discodeit.dto.user.JpaUserResponse;
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
