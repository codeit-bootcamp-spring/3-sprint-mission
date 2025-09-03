package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MessageCreatedEvent(
    UUID id,
    Instant createdAt,
    Instant updatedAt,
    String content,
    UUID channelId,
    UserDto author,
    List<BinaryContentDto> attachments
) {

}