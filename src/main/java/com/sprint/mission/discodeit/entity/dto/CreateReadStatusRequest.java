package com.sprint.mission.discodeit.entity.dto;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.time.Instant;
import java.util.UUID;

public record CreateReadStatusRequest(
        User user,
        Channel channel,
        Instant lastReadAt
) {
}
