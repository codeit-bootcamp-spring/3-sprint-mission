package com.sprint.mission.discodeit.dto.request;

import java.util.UUID;

public record MessageUpdateRequest(
    UUID messageId,
    String newContent
) {

}
