package com.sprint.mission.discodeit.service.DTO.Request;

import java.util.UUID;

public record UserFindRequest(
        UUID userId,
        String username,
        String email,
        boolean online,
        Byte[] portrait
) {}
