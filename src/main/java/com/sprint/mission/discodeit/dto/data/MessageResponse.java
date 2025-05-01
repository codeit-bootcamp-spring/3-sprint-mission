package com.sprint.mission.discodeit.dto.data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MessageResponse(
    UUID id,
    String content,
    UUID userId,
    UUID channelId,
    Instant createdAt,
    List<AttachmentResponse> attachments
) {

  public record AttachmentResponse(
      UUID id,
      String fileName,
      String mimeType,
      long size
  ) {

  }
}
