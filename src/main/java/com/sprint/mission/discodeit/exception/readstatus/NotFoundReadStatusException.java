package com.sprint.mission.discodeit.exception.readstatus;

import com.sprint.mission.discodeit.entity.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class NotFoundReadStatusException extends ReadStatusException {

    public NotFoundReadStatusException(UUID id) {
        super(
                ErrorCode.READ_STATUS_NOT_FOUND.getMessage() + " id: " + id,
                ErrorCode.READ_STATUS_NOT_FOUND,
                Map.of("readStatusId", id)
        );
    }
}
