package com.sprint.mission.discodeit.exception.readStatus;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class ReadStatusAlreadyExistsException extends ReadStatusException {

    public ReadStatusAlreadyExistsException(UUID userId, UUID channelId) {
        super(ErrorCode.ReadStatus_Already_Exists,
            Map.of("userId", userId, "channelId", channelId));
    }
}
