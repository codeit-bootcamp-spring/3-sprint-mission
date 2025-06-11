package com.sprint.mission.discodeit.dto.message.request;

import jakarta.validation.constraints.NotNull;

/**
 * packageName    : com.sprint.mission.discodeit.Dto.message
 * fileName       : MessageUpdateRequest
 * author         : doungukkim
 * date           : 2025. 4. 28.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 28.        doungukkim       최초 생성
 */

public record MessageUpdateRequest(
        @NotNull String newContent) { }
