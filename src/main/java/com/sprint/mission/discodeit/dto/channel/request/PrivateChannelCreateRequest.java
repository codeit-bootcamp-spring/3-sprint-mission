package com.sprint.mission.discodeit.dto.channel.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.Set;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.Dto.user
 * fileName       : PrivateChannelCreateRequest
 * author         : doungukkim
 * date           : 2025. 4. 25.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 25.        doungukkim       최초 생성
 */

public record PrivateChannelCreateRequest(
    @NotEmpty (message = "request must need one or more ids") Set<UUID> participantIds
) { }



