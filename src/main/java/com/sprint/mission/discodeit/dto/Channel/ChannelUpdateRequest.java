package com.sprint.mission.discodeit.dto.Channel;

import com.sprint.mission.discodeit.entity.ChannelType;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ChannelUpdateRequest(
        UUID id,
        ChannelType type,
        String name,
        String description

) {

}
