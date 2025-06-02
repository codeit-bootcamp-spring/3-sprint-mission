package com.sprint.mission.discodeit.dto.channel;

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

public record PrivateChannelCreateRequest(Set<String> participantIds) {
    public PrivateChannelCreateRequest {
        if ((participantIds == null) || (participantIds.isEmpty())) {
            throw new RuntimeException("request must need one or more ids");
        }
    }
}



