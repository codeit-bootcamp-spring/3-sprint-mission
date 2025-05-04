package com.sprint.mission.discodeit.service.dto.Channel;

import com.sprint.mission.discodeit.entity.ChannelType;
import java.util.UUID;

public record ChannelUpdateRequest(
        UUID id,
        ChannelType type,
        String name,
        String description

) {

}
