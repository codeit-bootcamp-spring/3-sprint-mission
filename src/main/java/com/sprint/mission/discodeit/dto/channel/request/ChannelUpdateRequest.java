package com.sprint.mission.discodeit.dto.channel.request;

import jakarta.validation.constraints.NotNull;

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
