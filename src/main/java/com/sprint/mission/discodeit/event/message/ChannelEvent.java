package com.sprint.mission.discodeit.event.message;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import java.time.Instant;

public record ChannelEvent(
    String eventName,
    ChannelDto data,
    Instant createdAt
){}
