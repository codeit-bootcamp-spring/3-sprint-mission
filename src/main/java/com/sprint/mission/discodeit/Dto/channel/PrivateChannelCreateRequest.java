package com.sprint.mission.discodeit.Dto.channel;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.Setter;

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

public record PrivateChannelCreateRequest(Set<String> userIds) {
    @JsonCreator
    public PrivateChannelCreateRequest {
        if ((userIds == null) || (userIds.isEmpty())) {
            throw new RuntimeException("request must need one or more ids");
        }
    }
}



