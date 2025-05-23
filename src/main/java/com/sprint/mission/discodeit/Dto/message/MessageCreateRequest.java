package com.sprint.mission.discodeit.Dto.message;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.Dto.message
 * fileName       : MessageCreateDto
 * author         : doungukkim
 * date           : 2025. 4. 28.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 28.        doungukkim       최초 생성
 */

public record MessageCreateRequest(
        @NotNull String content,
        @NotNull UUID channelId,
        @NotNull UUID authorId
) { }
