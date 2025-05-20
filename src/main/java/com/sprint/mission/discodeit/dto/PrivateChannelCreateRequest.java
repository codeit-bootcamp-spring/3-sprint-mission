package com.sprint.mission.discodeit.dto;

import java.util.List;
import java.util.UUID;

public record PrivateChannelCreateRequest(List<UUID> participantIds, UUID ownerId) {

  /*이미 정의된 request에서 ownerId를 따로 받지 않으므로 default value 설정 */
  public PrivateChannelCreateRequest(List<UUID> participantIds) {
    this(participantIds, null);
  }
}

