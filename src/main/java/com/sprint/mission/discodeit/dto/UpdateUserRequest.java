package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entitiy.User;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record UpdateUserRequest(UUID id,String username, String password, String email, Map<UUID, User> friends) {
}
