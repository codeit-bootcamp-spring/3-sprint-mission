package com.sprint.mission.discodeit.dto.message.response;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentResponse;
import com.sprint.mission.discodeit.dto.user.UserResponse;
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
public record MessageResponse(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        String content,
        UUID channelId,
        UserResponse author,
        List<BinaryContentResponse> attachments
) {
}
