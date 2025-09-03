package com.sprint.mission.discodeit.dto.data;

import jakarta.annotation.Nullable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MessageDto(
    UUID id,
    Instant createdAt,
    Instant updatedAt,
    String content,
    UUID channelId,
    UserDto author,
    @Nullable
    List<BinaryContentDto> attachments
) {

}
