package com.sprint.mission.discodeit.dto.request;

import java.util.UUID;

public record MessageUpdateRequest(
        UUID channelId,
        String password,
        UUID messageId,
        UUID senderId,
        String newMessageContent
) { }
