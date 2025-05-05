package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.entity.BinaryContent.ContentType;
import java.util.UUID;

public record BinaryContentCreateRequest(
    byte[] data,
    String fileName,
    String mimeType,
    ContentType contentType,
    UUID userId,
    UUID messageId
) {

}
