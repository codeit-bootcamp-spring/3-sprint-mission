package com.sprint.mission.discodeit.service.dto.Message;

import java.util.UUID;
import lombok.Builder;

@Builder
public record MessageCreateRequest(
        String content,
        UUID channelId,
        UUID authorId
) {
}
