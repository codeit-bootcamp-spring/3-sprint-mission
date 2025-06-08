package com.sprint.mission.discodeit.dto.request;

import java.util.UUID;

public record PublicChannelCreateRequest(String name, String description, UUID ownerId) {

  /*이미 정의된 request에서 ownerId를 따로 받지 않으므로 default value 설정 */
  public PublicChannelCreateRequest(String name, String description) {
    this(name, description, null);
  }
}

