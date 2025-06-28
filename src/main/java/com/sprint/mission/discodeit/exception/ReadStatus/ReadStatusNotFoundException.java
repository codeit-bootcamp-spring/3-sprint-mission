package com.sprint.mission.discodeit.exception.ReadStatus;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;

@Getter
public class ReadStatusNotFoundException extends ReadStatusException {

    private final UUID readStatusId;

    public ReadStatusNotFoundException(UUID readStatusId) {
        super(
                ErrorCode.READ_STATUS_NOT_FOUND,
                Map.of("readStatusId", readStatusId)
        );
        this.readStatusId = readStatusId;
    }
}
