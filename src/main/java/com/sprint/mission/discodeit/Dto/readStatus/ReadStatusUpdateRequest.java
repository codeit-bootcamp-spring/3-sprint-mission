package com.sprint.mission.discodeit.Dto.readStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

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

public record ReadStatusUpdateRequest
        (@NotNull Instant newLastReadAt) { }
