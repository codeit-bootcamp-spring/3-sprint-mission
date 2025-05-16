package com.sprint.mission.discodeit.dto.request;

import java.util.List;
import java.util.UUID;

public record MessageCreateRequest(
        UUID senderId,
        UUID channelId,
        String password,
        String messageContent
) { }
