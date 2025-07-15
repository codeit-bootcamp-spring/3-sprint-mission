package com.sprint.mission.discodeit.exception.readStatus;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.UUID;

public class ReadStatusAlreadyExistException extends ReadStatusException {

    public ReadStatusAlreadyExistException() {
        super(ErrorCode.READ_STATUS_ALREADY_EXISTS);
    }

    public static ReadStatusAlreadyExistException withUserIdAndChannelId(UUID userId,
        UUID channelId) {
        ReadStatusAlreadyExistException exception = new ReadStatusAlreadyExistException();
        exception.addDetail("userId", userId);
        exception.addDetail("channelId", channelId);
        return exception;
    }
} 