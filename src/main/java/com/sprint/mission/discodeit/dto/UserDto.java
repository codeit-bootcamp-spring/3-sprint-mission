package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.User;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;

@Getter
public class UserDto {

    private final UUID id;
    private final Instant createdAt;
    private final String username;
    private final String email;
    private final boolean isOnline;
    private final UUID profileId;


    public UserDto(User user, boolean isOnline) {
        this.id = user.getId();
        this.createdAt = user.getCreatedAt();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.isOnline = isOnline;
        this.profileId = user.getProfileId();
    }
}