package com.sprint.mission.discodeit.storage.event;

import java.util.UUID;

public record BinaryContentCreatedEvent(
    UUID contentId,
    byte[] bytes,
    String fileName,
    String contentType,
    long size
) {}
