package com.sprint.mission.discodeit.dto.user;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record UserResponseDTO(UUID id, Instant createdAt, Instant updatedAt, String username,
                              String email,
                              UUID profileId, String introduction, boolean isLogin,
                              List<UUID> friends,
                              List<UUID> channels, List<UUID> message) {

}
