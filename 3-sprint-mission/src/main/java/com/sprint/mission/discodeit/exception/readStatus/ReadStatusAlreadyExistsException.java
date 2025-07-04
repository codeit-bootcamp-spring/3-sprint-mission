package com.sprint.mission.discodeit.exception.readStatus;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class ReadStatusAlreadyExistsException extends ReadStatusException {

  private UUID userId;
  private UUID channelId;

  public ReadStatusAlreadyExistsException(UUID userId, UUID channelId) {
    super(ErrorCode.DUPLICATE_READ_STATUS,
        Map.of("userId", userId, "channelId", channelId));
    this.userId = userId;
    this.channelId = channelId;
  }
}
