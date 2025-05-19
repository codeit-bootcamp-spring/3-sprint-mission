package com.sprint.mission.discodeit.dto.Message;

import java.util.UUID;

public record MessageCreateRequest(
        String content,
        UUID authorId,
        UUID channelId
) {

}
