package com.sprint.mission.discodeit.Dto.channel;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.Objects;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.Dto.channel
 * fileName       : ChannelUpdateRequest
 * author         : doungukkim
 * date           : 2025. 4. 27.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 27.        doungukkim       최초 생성
 */

public record ChannelUpdateRequest(
        @NotNull String newName,
        @NotNull String newDescription
        ) { }
