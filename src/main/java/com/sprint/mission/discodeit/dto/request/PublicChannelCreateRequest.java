package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record PublicChannelCreateRequest(
    @NotBlank(message = "채널이름은 필수입니다")
    String name,
    String description,
    UUID ownerId) {

  /*이미 정의된 request에서 ownerId를 따로 받지 않으므로 default value 설정 */
  public PublicChannelCreateRequest(String name, String description) {
    this(name, description, null);
  }
}

