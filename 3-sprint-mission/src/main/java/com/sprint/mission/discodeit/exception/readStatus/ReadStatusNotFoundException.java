package com.sprint.mission.discodeit.exception.readStatus;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class ReadStatusNotFoundException extends ReadStatusException {

  private UUID readStatusId;
  private UUID userId;
  private UUID channelId;

  public ReadStatusNotFoundException(UUID readStatusId) {
    super(ErrorCode.READ_STATUS_NOT_FOUND,
        Map.of("readStatusId", readStatusId));
    this.readStatusId = readStatusId;
  }

  public ReadStatusNotFoundException(UUID userId, UUID channelId) {
    super(ErrorCode.READ_STATUS_NOT_FOUND,
        Map.of("userId", userId, "channelId", channelId));
    this.userId = userId;
    this.channelId = channelId;
  }
}
