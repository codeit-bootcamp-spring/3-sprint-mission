package com.sprint.mission.discodeit.Dto.binaryContent;

import java.util.UUID;

/**
 * PackageName  : com.sprint.mission.discodeit.Dto.binaryContent
 * FileName     : BinaryContentResponseWithoutBytes
 * Author       : dounguk
 * Date         : 2025. 5. 28.
 */
public record JpaBinaryContentResponse(
                UUID id,
                String fileName,
                Long size,
                String contentType
) {
}
