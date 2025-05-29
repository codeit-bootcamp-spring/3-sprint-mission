package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.Message;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record MessageResponse(
    UUID id,
    Instant createdAt,
    Instant updatedAt,
    String content,
    UUID channelId,
    UserResponse author,
    List<BinaryContentResponse> attachments
) {

  public static MessageResponse from(Message message) {
    return new MessageResponse(
        message.getId(),
        message.getCreatedAt(),
        message.getUpdatedAt(),
        message.getContent(),
        message.getChannel().getId(),
        UserResponse.from(message.getAuthor(), null),
        message.getAttachments().stream()
            .map(att -> BinaryContentResponse.from(att.getBinaryContent()))
            .collect(Collectors.toList())
    );
  }
}
