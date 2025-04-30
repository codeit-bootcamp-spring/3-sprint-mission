package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entitiy.User;
import com.sprint.mission.discodeit.entitiy.UserStatus;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;


public record FindUserRequest(UUID id, UUID profileId, Instant createdAt, Instant updatedAt, String username, String email, Map<UUID,User> friends,
                              UserStatus userStatus) {}
