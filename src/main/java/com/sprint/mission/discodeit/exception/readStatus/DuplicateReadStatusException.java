package com.sprint.mission.discodeit.exception.readStatus;

import com.sprint.mission.discodeit.dto.ErrorCode;
import com.sprint.mission.discodeit.dto.ResponseMessage;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class DuplicateReadStatusException extends ReadStatusException {
    public DuplicateReadStatusException(UUID userId, UUID channelId) {
        super(
                Instant.now(),
                ErrorCode.DUPLICATE_READ_STATUS,
                Map.of(
                        "userId", userId,
                        "channelId", channelId
                )
        );
    }
}
