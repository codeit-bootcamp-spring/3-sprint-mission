package com.sprint.mission.discodeit.entity.dto;

import com.sprint.mission.discodeit.entity.Channel;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public record ChannelDTO(
        Channel channel,
        Instant lastReadAt,
        Optional<List<UUID>> userIds
) { }
