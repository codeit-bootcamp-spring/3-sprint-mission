package com.sprint.mission.discodeit.dto.request;

import java.util.UUID;

public record MessageRequest(
) {

  public record Create(
      UUID channelId,
      String content,
      UUID authorId
  ) {

  }

  public record Update(
      String newContent
  ) {

  }

}
