package com.sprint.mission.discodeit.dto.channel.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.*;

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
    @NotEmpty (message = "request must need one or more ids") Set<String> participantIds
) { }



