package com.sprint.mission.discodeit.dto.authService;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentResponse;

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
        BinaryContentResponse profile,
        boolean online

) {

}
