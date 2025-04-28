package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record UserCreateRequest(String name, String email, String password, UUID profileId) {
}

