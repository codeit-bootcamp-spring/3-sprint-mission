package com.sprint.mission.discodeit.service.DTO.Request;

import java.util.List;
import java.util.UUID;

public record PrivateChannelCreateRequest(
        List<UUID> inChannelUsers
) {}
