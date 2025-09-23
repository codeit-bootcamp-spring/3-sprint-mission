package com.sprint.mission.discodeit.event;

import java.util.UUID;

public record BinaryStorageFailedEvent(
    UUID binaryContentId,
    String requestId,
    String errorSummary
) {

}
