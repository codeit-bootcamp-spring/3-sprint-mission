package com.sprint.mission.discodeit.dto.request;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public record MessageCreateRequest(
    String content,
    UUID userId,
    UUID channelId,
    Optional<Set<UUID>> attachmentIds
) {

}