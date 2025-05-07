package com.sprint.mission.discodeit.dto.user;

public record CreateUserRequest(String username,
                                String email,
                                String password) {
}
