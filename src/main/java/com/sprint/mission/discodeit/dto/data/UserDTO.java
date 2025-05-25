package com.sprint.mission.discodeit.dto.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class UserDTO {
    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    private String username;
    private String email;
    private UUID profileId;
    private Boolean online;

    @Override
    public String toString() {
        return "[UserDTO] {" + username + " id=" + id + ", email=" + email + ", loggedIn=" + online + "}";
    }
}

