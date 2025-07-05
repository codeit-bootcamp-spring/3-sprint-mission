package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.BinaryContent;
import java.util.UUID;

public record BinaryContentResponse(
    UUID id,
    String fileName,
    String contentType,
    Long size
) {

  public static BinaryContentResponse from(BinaryContent content) {
    return new BinaryContentResponse(
        content.getId(),
        content.getFileName(),
        content.getContentType(),
        content.getSize()
    );
  }
}
