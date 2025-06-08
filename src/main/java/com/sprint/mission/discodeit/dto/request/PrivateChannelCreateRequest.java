package com.sprint.mission.discodeit.dto.request;

import java.util.List;
import java.util.UUID;

public record PrivateChannelCreateRequest(
    // 2개의 속성 제거( name, description )
    List<UUID> participantIds
) {

}
