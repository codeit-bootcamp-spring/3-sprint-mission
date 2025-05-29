package com.sprint.mission.discodeit.Dto.authService;

import com.sprint.mission.discodeit.Dto.binaryContent.JpaBinaryContentResponse;

import java.time.Instant;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.Dto.authService fileName       : LoginResponse
 * author         : doungukkim date           : 2025. 4. 29. description    :
 * =========================================================== DATE              AUTHOR
 * NOTE ----------------------------------------------------------- 2025. 4. 29.        doungukkim
 * 최초 생성
 */
public record LoginResponse(
        UUID id,
        String username,
        String email,
        JpaBinaryContentResponse profile,
        boolean online

) {

}
