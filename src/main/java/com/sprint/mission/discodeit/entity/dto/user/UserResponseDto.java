package com.sprint.mission.discodeit.entity.dto.user;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Setter
@Getter
@ToString
public class UserResponseDto {
    private UUID id;
    private String username;
    private boolean isOnline;
    private byte[] userProfile;
    private Instant createdAt;
    private Instant updatedAt;

    public UserResponseDto(UUID id, String username, boolean isOnline, byte[] userProfile, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.username = username;
        this.isOnline = isOnline;
        this.userProfile = userProfile;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UserResponseDto(UUID id, String username, boolean isOnline, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.username = username;
        this.isOnline = isOnline;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
