package com.sprint.mission.discodeit.Dto.binaryContent;

import java.time.Instant;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.Dto.binaryContent
 * fileName       : FindBinaryContentResponse
 * author         : doungukkim
 * date           : 2025. 5. 16.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 5. 16.        doungukkim       최초 생성
 */
public record FindBinaryContentResponse(
        UUID id,
        String fileName,
        long size,
        String contentType
) {
}
