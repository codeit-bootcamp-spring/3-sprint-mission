package com.sprint.mission.discodeit.exception.readStatus;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class ReadStatusNotFoundException extends ReadStatusException {

    public ReadStatusNotFoundException(UUID readStatusId) {
        super(ErrorCode.BinaryContent_Not_Found, Map.of("readStatusId", readStatusId));
    }
}
