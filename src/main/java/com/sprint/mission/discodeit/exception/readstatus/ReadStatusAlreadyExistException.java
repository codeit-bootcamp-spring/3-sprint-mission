package com.sprint.mission.discodeit.exception.readstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class ReadStatusAlreadyExistException extends ReadStatusException {

    public ReadStatusAlreadyExistException(UUID channelId, UUID userId) {
        super(ErrorCode.READ_STATUS_ALREADY_EXIST, Map.of("읽음상태를 생성하려고 한 채널: ", channelId, "읽음상태를 생성하려고 한 유저: ", userId));
    }
}
