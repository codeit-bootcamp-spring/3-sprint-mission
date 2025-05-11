package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.entity.ChannelType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public final class PrivateChannelCreateRequest {
    // 2개의 속성 제거( name, description )
    private List<UUID> userIds;
}
