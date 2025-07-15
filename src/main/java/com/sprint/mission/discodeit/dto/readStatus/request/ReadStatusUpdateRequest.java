package com.sprint.mission.discodeit.dto.readStatus.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

/**
 * packageName    : com.sprint.mission.discodeit.Dto.userStatus
 * fileName       : ReadStatusUpdateRequest
 * author         : doungukkim
 * date           : 2025. 4. 28.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 28.        doungukkim       최초 생성
 */

public record ReadStatusUpdateRequest(@NotNull Instant newLastReadAt) { }
