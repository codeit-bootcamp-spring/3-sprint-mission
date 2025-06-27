package com.sprint.mission.discodeit.exception.readstatus;

import com.sprint.mission.discodeit.entity.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class ReadStatusAlreadyExistsException extends ReadStatusException {

    public ReadStatusAlreadyExistsException(UUID userId, UUID channelId) {
        super(
                ErrorCode.READ_STATUS_ALREADY_EXIST.getMessage() + " userId: " + userId + " channelId: "
                        + channelId,
                ErrorCode.READ_STATUS_ALREADY_EXIST,
                Map.of("channelId", channelId,
                        "userId", userId)
        );
    }
}
