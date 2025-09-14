package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.response.ChannelDto;

public record ChannelUpdateEvent(
    ChannelUpdateType changeType,
    ChannelDto dto
) { }
